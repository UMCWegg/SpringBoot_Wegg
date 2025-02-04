package umc.wegg.service.PlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Plan;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.AddressRepository;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.NotificationService.NotificationService;

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

    @Override
    public List<Plan> addPlan(PlanRequestDTO.PlanAddDTO request) {
        // planDates에 대해 각각 Plan을 생성하여 반환
        List<Plan> newPlans = PlanConverter.toPlan(request, userRepository, addressRepository);

        // 반환된 계획을 저장
        newPlans.forEach(planRepository::save);

        // 각 계획에 대해 알림 예약
        newPlans.forEach(plan -> scheduleNotifications(plan));

        return newPlans;
    }

    private void scheduleNotifications(Plan plan) {
        // 계획의 startTime을 가져옴
        LocalDateTime startTime = plan.getStartTime();
        LocalDateTime finishTime = plan.getFinishTime();
        // 10분 전 알림 예약
        LocalDateTime fiveMinutesBefore = startTime.minusMinutes(5);
        notificationService.scheduleNotification(plan.getUser(), NotificationType.PLACE_VERIFY, fiveMinutesBefore, "장소를 인증하고 공부를 시작하는데 5분 남았어요.", "/plans/" + plan.getId() + "/check");

        // 계획의 startTime에 알림 예약
        notificationService.scheduleNotification(plan.getUser(), NotificationType.PLACE_VERIFY, startTime, "시간이 다 되었습니다! 인증을 진행해주세요.", "/plans/" + plan.getId() + "/check");
        // startTime과 finishTime 사이의 랜덤 알림 예약
        long min = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long max = finishTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 랜덤한 시간 생성 (startTime과 finishTime 사이)
        long randomTimeMillis = min + (long) (Math.random() * (max - min));
        LocalDateTime randomTime = Instant.ofEpochMilli(randomTimeMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 랜덤 알림 예약
        notificationService.scheduleNotification(plan.getUser(), NotificationType.RANDOM_VERIFY, randomTime, "2분 안에 사진을 찍어 나의 공부를 인증하세요.", "/posts");
    }




    @Override
    public Plan updatePlan(Long planId, PlanRequestDTO.PlanUpdateDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (request.getStatus() != null) {
            existingPlan.setStatus(request.getStatus());
        }

        return planRepository.save(existingPlan);
    }

    @Override
    public Plan onoffPlan(Long planId, PlanRequestDTO.PlanOnoffDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (request.getPlanOn() != null) {
            existingPlan.setPlanOn(request.getPlanOn());
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
}
