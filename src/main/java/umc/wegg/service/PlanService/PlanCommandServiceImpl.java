package umc.wegg.service.PlanService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.AddressConverter;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Egg;
import umc.wegg.domain.Address;
import umc.wegg.domain.Plan;
import umc.wegg.domain.enums.EggStatus;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.AddressRepository;
import umc.wegg.repository.EggRepository;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.NotificationService.NotificationService;
import umc.wegg.util.RedisUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanCommandServiceImpl implements PlanCommandService{
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;
    private final EggRepository eggRepository;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final PlanQueryService planQueryService;

    @Override
    public List<PlanResponseDTO.PlanAddResultDTO> addPlan(PlanRequestDTO.PlanAddDTO request) {

        MapResponseDTO.SearchDTO.PlaceDetailDTO addressDetail = null;
        try {
            // Redis에서 JSON 데이터 가져오기
            String addressStr = redisUtil.getData(request.getPlaceName());
            if (addressStr != null) {
                addressDetail = objectMapper.readValue(addressStr, MapResponseDTO.SearchDTO.PlaceDetailDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 변환 실패 시 처리
        }

        Address address = addressRepository.findByPlaceName(request.getPlaceName()).orElse(null);
        if (address == null) {
            assert addressDetail != null;
            address = AddressConverter.toAddress(addressDetail);
            addressRepository.save(address);
        }

        // ✅ 컨버터가 이제 List<Plan>을 반환함
        List<Plan> newPlans = PlanConverter.toPlan(request, userRepository, address, planRepository);

        // ✅ 응답용 DTO 생성
        List<PlanResponseDTO.PlanAddResultDTO> resultDTOList = newPlans.stream()
                .map(plan -> {
                    String warningMessage = null;

                    // ✅ 날짜 비교 (startTime과 finishTime의 날짜가 다를 경우)
                    if (!plan.getStartTime().toLocalDate().equals(plan.getFinishTime().toLocalDate())) {
                        warningMessage = "랜덤 인증 시간이 다음날로 넘어갈 수 있습니다.";
                    }

                    return new PlanResponseDTO.PlanAddResultDTO(plan.getId(), plan.getCreatedAt(), warningMessage);
                })
                .collect(Collectors.toList());

        // 반환된 계획을 기반으로 Egg 생성
        newPlans.forEach(plan -> {
            Egg egg = Egg.builder()
                    .status(EggStatus.INTACT)
                    .plan(plan)
                    .build();

            eggRepository.save(egg);

            // Plan과 Egg를 연결
            plan.setEgg(egg);
            planRepository.save(plan);
        });

        // 각 계획에 대해 알림 예약
        newPlans.forEach(this::scheduleNotifications);

        return resultDTOList; // ✅ 응답용 DTO 반환
    }

    @Override
    public Plan updatePlan(Long planId, PlanRequestDTO.PlanUpdateDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // PlanConverter를 사용하여 기존 Plan 엔티티를 업데이트
        Plan updatedPlan = PlanConverter.toPlanUpdate(request, existingPlan, addressRepository);

        // 업데이트된 Plan을 저장하고 반환
        return planRepository.save(updatedPlan);
    }

    @Override
    public Plan onoffPlan(Long planId, PlanRequestDTO.PlanOnoffDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (request.getPlanOn() != null) {
            existingPlan.setPlanOn(request.getPlanOn());

            // Plan과 연결된 Egg 객체 가져오기
            Egg egg = existingPlan.getEgg();
            if (egg != null) {
                // planOn이 true면 INTACT, false면 INACTIVE로 변경
                egg.setStatus(request.getPlanOn() ? EggStatus.INTACT : EggStatus.INACTIVE);
            }
        }

        return planRepository.save(existingPlan);
    }

    @Override
    public Plan statusPlan(Long planId, PlanRequestDTO.PlanStatusDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (request.getPlanStatus() != null) {
            existingPlan.setStatus(request.getPlanStatus());
        }

        return planRepository.save(existingPlan);
    }

    @Override
    public PlanResponseDTO.PlanDeleteResponseDTO deletePlan(Long planId) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));

        // Deleting the plan
        planRepository.deleteById(planId);

        // Return the response DTO after deletion
        return PlanConverter.toPlanDeleteResponseDTO(existingPlan);
    }

    private void scheduleNotifications(Plan plan) {
        // planOn이 false면 알림을 생성하지 않음
        if (!Boolean.TRUE.equals(plan.getPlanOn())) {
            return;
        }
        // 계획의 startTime을 가져옴
        LocalDateTime startTime = plan.getStartTime();
        LocalDateTime finishTime = plan.getFinishTime();
        // 10분 전 알림 예약
        LocalDateTime fiveMinutesBefore = startTime.minusMinutes(5);
        notificationService.scheduleNotification(plan.getUser(), NotificationType.PLACE_VERIFY, fiveMinutesBefore, "장소를 인증하고 공부를 시작하는데 5분 남았어요.", "/plans/" + plan.getId() + "/check", null);

        // 계획의 startTime에 알림 예약
        notificationService.scheduleNotification(plan.getUser(), NotificationType.PLACE_VERIFY, startTime, "시간이 다 되었습니다! 인증을 진행해주세요.", "/plans/" + plan.getId() + "/check", null);
        // startTime과 finishTime 사이의 랜덤 알림 예약
        long min = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long max = finishTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 랜덤한 시간 생성 (startTime과 finishTime 사이)
        long randomTimeMillis = min + (long) (Math.random() * (max - min));
        LocalDateTime randomTime = Instant.ofEpochMilli(randomTimeMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 랜덤 알림 예약
        notificationService.scheduleNotification(plan.getUser(), NotificationType.RANDOM_VERIFY, randomTime, "2분 안에 사진을 찍어 나의 공부를 인증하세요.", "/posts", null);
        // **장소 인증 후 계획이 실패하는 경우 알림 예약**
        planQueryService.schedulePlanVerification(plan);
    }
}
