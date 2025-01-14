package umc.wegg.service.PlanService;

import umc.wegg.domain.Plan;

import java.util.List;

public interface PlanQueryService {
    List<Plan> getPlansByUserId(Long userId); // 특정 userId의 계획 조회
    List<Plan> getAllPlans(); // 전체 계획 조회
}
