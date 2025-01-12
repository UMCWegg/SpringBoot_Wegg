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
                .user(user)  // User 객체 설정
                .build();
    }

}