package umc.wegg.service.PlanService;

import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanRequestDTO;

public interface PlanCommandService {
    Plan addPlan(PlanRequestDTO.PlanAddDTO request);
    Plan updatePlan(Long planId, PlanRequestDTO.PlanUpdateDTO request); // updateTodo 추가
}
