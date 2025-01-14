package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Plan;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.service.PlanService.PlanCommandService;
import umc.wegg.service.PlanService.PlanQueryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanRestController {
    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    @PostMapping("/add")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> join(@RequestBody @Valid PlanRequestDTO.PlanAddDTO request){
        Plan plan = planCommandService.addPlan(request);
        return ApiResponse.onSuccess(PlanConverter.toPlanAddResultDTO(plan));
    }

    @PatchMapping("/{plan_id}")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> updateTodo(
            @PathVariable("plan_id") Long planId,
            @RequestBody @Valid PlanRequestDTO.PlanUpdateDTO request) {
        Plan updatedPlan = planCommandService.updatePlan(planId, request);
        return ApiResponse.onSuccess(PlanConverter.toPlanAddResultDTO(updatedPlan));
    }

    @GetMapping
    public ApiResponse<List<PlanResponseDTO.PlanDetailDTO>> getPlans(
            @RequestParam(required = false) Long userId) {
        List<Plan> plans = (userId != null)
                ? planQueryService.getPlansByUserId(userId)
                : planQueryService.getAllPlans();

        List<PlanResponseDTO.PlanDetailDTO> response = plans.stream()
                .map(PlanConverter::toPlanDetailDTO)
                .toList();

        return ApiResponse.onSuccess(response);
    }

    // 사용자가 계획에 설정된 가게 내에 있는지 확인
    @GetMapping("/{plan_id}/check")
    public boolean checkUserLocation(
            @PathVariable("plan_id") Long planId,
            @RequestParam Long userId) {

        return planQueryService.isUserInPlan(planId, userId);
    }
}
