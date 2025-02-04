package umc.wegg.service.PlanService;

import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;

import java.util.List;

import java.util.List;

public interface PlanCommandService {
    List<Plan> addPlan(PlanRequestDTO.PlanAddDTO request);
    Plan updatePlan(Long planId, PlanRequestDTO.PlanUpdateDTO request); // updateTodo 추가
    Plan onoffPlan(Long planId, PlanRequestDTO.PlanOnoffDTO request);
    PlanResponseDTO.PlanDeleteResponseDTO deletePlan(Long planId); // 특정 계획 삭제
}
