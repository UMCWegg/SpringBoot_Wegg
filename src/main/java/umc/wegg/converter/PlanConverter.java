package umc.wegg.converter;

import lombok.RequiredArgsConstructor;
import umc.wegg.domain.Plan;
import umc.wegg.domain.User;
import umc.wegg.domain.Address;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.enums.LateStatus;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.repository.AddressRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlanConverter {

    // Plan 추가 결과 DTO 변환
    public static PlanResponseDTO.PlanAddResultDTO toPlanAddResultDTO(Plan plan){
        return PlanResponseDTO.PlanAddResultDTO.builder()
                .planId(plan.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static PlanResponseDTO.PlanUpdateResultDTO toPlanUpdateResultDTO(Plan plan){
        return PlanResponseDTO.PlanUpdateResultDTO.builder()
                .planId(plan.getId())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<List<Plan>> toPlan(PlanRequestDTO.PlanAddDTO request,
                                                 UserRepository userRepository,
                                                 Address address,
                                                 PlanRepository planRepository) {
        PlanStatus status = request.getStatus() != null ? request.getStatus() : PlanStatus.YET;
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Plan> planList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate maxAllowedDate = today.plusDays(14);

        for (LocalDate planDate : request.getPlanDates()) {
            // ✅ 과거 날짜 체크
            if (planDate.isBefore(today)) {
                return ApiResponse.onFailure("FAIL", "이미 날짜가 지났습니다.", null);
            }
            // ✅ 2주 후 초과 체크
            if (planDate.isAfter(maxAllowedDate)) {
                return ApiResponse.onFailure("FAIL", "2주 뒤까지의 계획만 설정 가능합니다.", null);
            }

            LocalDateTime startTime = LocalDateTime.of(planDate, request.getStartTime())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
            LocalDateTime finishTime = LocalDateTime.of(planDate, request.getFinishTime())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();

            if (finishTime.isBefore(startTime)) {
                finishTime = finishTime.plusDays(1);
            }

            // ✅ 일정 겹침 체크
            boolean hasConflict = planRepository.existsByUserAndStartTimeBeforeAndFinishTimeAfter(
                    user, finishTime, startTime);

            if (hasConflict) {
                return ApiResponse.onFailure("FAIL", "해당 시간대에 이미 일정이 존재합니다.", null);
            }

            // Plan 객체 생성 및 저장
            Plan newPlan = Plan.builder()
                    .status(status)
                    .startTime(startTime)
                    .finishTime(finishTime)
                    .user(user)
                    .lateTime(request.getLateTime() != null ? request.getLateTime() : LateStatus.ZERO)
                    .address(address)
                    .planOn(request.getPlanOn() != null ? request.getPlanOn() : true)
                    .planDate(planDate)
                    .build();

            planRepository.save(newPlan);
            planList.add(newPlan);
        }

        return ApiResponse.onSuccess(planList); // 성공 응답
    }
    // PlanRequestDTO를 사용하여 기존 Plan을 업데이트하는 메서드 추가
    public static Plan toPlanUpdate(PlanRequestDTO.PlanUpdateDTO requestDTO, Plan existingPlan, AddressRepository addressRepository) {
        // 주어진 requestDTO의 값을 기존 Plan 엔티티에 업데이트
        if (requestDTO.getStartTime() != null) {
            existingPlan.setStartTime(LocalDateTime.of(existingPlan.getPlanDate(), requestDTO.getStartTime().truncatedTo(ChronoUnit.MINUTES)));
        }

        if (requestDTO.getFinishTime() != null) {
            existingPlan.setFinishTime(LocalDateTime.of(existingPlan.getPlanDate(), requestDTO.getFinishTime().truncatedTo(ChronoUnit.MINUTES)));
        }

        if (requestDTO.getLateTime() != null) {
            existingPlan.setLateTime(requestDTO.getLateTime());
        }

        if (requestDTO.getAddressId() != null) {
            Address address = addressRepository.findById(requestDTO.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            existingPlan.setAddress(address);  // Address를 업데이트
        }

        return existingPlan; // 업데이트된 Plan 반환
    }

    public static PlanResponseDTO.PlanStatusDTO toPlanStatusDTO(Plan plan) {
        return PlanResponseDTO.PlanStatusDTO.builder()
                .planId(plan.getId())
                .planStatus(plan.getStatus())
                .build();
    }

    // Plan 상세 DTO 변환
    public static PlanResponseDTO.PlanDetailDTO toPlanDetailDTO(Plan plan) {
        return PlanResponseDTO.PlanDetailDTO.builder()
                .planId(plan.getId())
                .planDate(plan.getPlanDate())
                .startTime(plan.getStartTime().toLocalTime().truncatedTo(ChronoUnit.MINUTES)) // 초, 나노초 제거
                .finishTime(plan.getFinishTime().toLocalTime().truncatedTo(ChronoUnit.MINUTES)) // 초, 나노초 제거
                .lateTime(plan.getLateTime())
                .Onoff(plan.getPlanOn())
                .latitude(plan.getAddress().getLatitude()) // Address의 latitude 사용
                .longitude(plan.getAddress().getLongitude()) // Address의 longitude 사용
                .placeName(plan.getAddress().getPlaceName()) // Address의 address 사용
                .userId(plan.getUser().getId()) // Plan에 User가 연관되어 있다고 가정
                .build();
    }

    public static PlanResponseDTO.PlanDeleteResponseDTO toPlanDeleteResponseDTO(Plan plan) {
        return PlanResponseDTO.PlanDeleteResponseDTO.builder()
                .planId(plan.getId())
                .planDate(plan.getPlanDate())
                .startTime(LocalTime.from(plan.getStartTime()))
                .address(plan.getAddress().getAddress())
                .build();
    }
}