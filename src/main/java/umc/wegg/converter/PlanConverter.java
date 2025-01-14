package umc.wegg.converter;

import umc.wegg.domain.Plan;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.UserRepository;

import java.time.LocalDateTime;

public class PlanConverter {
    public static PlanResponseDTO.PlanAddResultDTO toPlanAddResultDTO(Plan plan){
        return PlanResponseDTO.PlanAddResultDTO.builder()
                .planId(plan.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Plan toPlan(PlanRequestDTO.PlanAddDTO request, UserRepository userRepository) {
        // status가 null이면 기본값으로 YET
        PlanStatus status = request.getStatus() != null ? request.getStatus() : PlanStatus.YET;

        // userId로 User 객체를 찾아서 설정
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Plan.builder()
                .status(status)
                .replay(request.getReplay())
                .startTime(request.getStartTime())
                .finishTime(request.getFinishTime())
                .user(user)
                .lateTime(request.getLateTime() != null ? request.getLateTime() : 0) // null일 경우 기본값 설정
                .latitude(request.getLatitude() != null ? request.getLatitude() : 0.0f) // null일 경우 기본값 설정
                .longitude(request.getLongitude() != null ? request.getLongitude() : 0.0f) // null일 경우 기본값 설정
                .address(request.getAddress())
                .build();
    }
    public static PlanResponseDTO.PlanDetailDTO toPlanDetailDTO(Plan plan) {
        return PlanResponseDTO.PlanDetailDTO.builder()
                .planId(plan.getId())
                .status(plan.getStatus())
                .replay(plan.getReplay())
                .startTime(plan.getStartTime())
                .finishTime(plan.getFinishTime())
                .lateTime(plan.getLateTime())
                .latitude(plan.getLatitude())
                .longitude(plan.getLongitude())
                .address(plan.getAddress())
                .userId(plan.getUser().getId()) // Plan에 User가 연관되어 있다고 가정
                .build();
    }


}