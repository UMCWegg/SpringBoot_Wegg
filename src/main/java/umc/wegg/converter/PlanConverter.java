package umc.wegg.converter;

import lombok.RequiredArgsConstructor;
import umc.wegg.domain.Plan;
import umc.wegg.domain.User;
import umc.wegg.domain.Address;
import umc.wegg.domain.enums.LateStatus;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.UserRepository;
import umc.wegg.repository.AddressRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    // PlanRequestDTO에서 Plan으로 변환
    public static List<Plan> toPlan(PlanRequestDTO.PlanAddDTO request, UserRepository userRepository, Address address) {
        PlanStatus status = request.getStatus() != null ? request.getStatus() : PlanStatus.YET;
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 시간을 포맷하는 DateTimeFormatter 설정
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // 각 날짜에 대해 계획을 추가하는 방식으로 변경
        return request.getPlanDates().stream()
                .map(planDate -> Plan.builder()
                        .status(status)
                        .startTime(LocalDateTime.of(planDate, request.getStartTime().truncatedTo(ChronoUnit.MINUTES))) // 초와 나노초를 제거
                        .finishTime(LocalDateTime.of(planDate, request.getFinishTime().truncatedTo(ChronoUnit.MINUTES))) // 초와 나노초를 제거
                        .user(user)
                        .lateTime(request.getLateTime() != null ? request.getLateTime() : LateStatus.ZERO)
                        .address(address) // Address 정보 할당
                        .planOn(request.getPlanOn() != null ? request.getPlanOn() : true)
                        .planDate(planDate) // 각 날짜에 해당하는 planDate 추가
                        .build())
                .collect(Collectors.toList());
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
                .status(plan.getStatus())
                .startTime(plan.getStartTime().truncatedTo(ChronoUnit.MINUTES)) // 초, 나노초 제거
                .finishTime(plan.getFinishTime().truncatedTo(ChronoUnit.MINUTES)) // 초, 나노초 제거
                .lateTime(plan.getLateTime())
                .latitude(plan.getAddress().getLatitude()) // Address의 latitude 사용
                .longitude(plan.getAddress().getLongitude()) // Address의 longitude 사용
                .address(plan.getAddress().getAddress()) // Address의 address 사용
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