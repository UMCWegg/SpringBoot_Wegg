package umc.wegg.service.PlanService;

import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanRequestDTO;

public interface PlanCommandService {
    Plan addPlan(PlanRequestDTO.PlanAddDTO request);
    Plan updatePlan(Long planId, PlanRequestDTO.PlanUpdateDTO request); // updateTodo 추가
    Plan onoffPlan(Long planId, PlanRequestDTO.PlanOnoffDTO request);
    void deletePlan(Long planId); // 특정 계획 삭제
}
