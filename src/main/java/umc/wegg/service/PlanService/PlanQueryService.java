package umc.wegg.service.PlanService;

import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanResponseDTO;

import java.util.List;

public interface PlanQueryService {
    List<Plan> getPlansByUserId(Long userId); // 특정 userId의 계획 조회
    List<Plan> getAllPlans(); // 전체 계획 조회
    PlanResponseDTO.LocationVerificationResponseDTO isUserInPlan(Long planId, Long userId); // 특정 plan에서 사용자 위치 체크
    void schedulePlanVerification(Plan plan);
}
