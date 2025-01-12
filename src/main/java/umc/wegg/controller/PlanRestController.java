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

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanRestController {
    private final PlanCommandService planCommandService;

    @PostMapping("/add")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> join(@RequestBody @Valid PlanRequestDTO.PlanAddDTO request){
        return null;
    }

    @PatchMapping("/{plan_id}")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> updateTodo(
            @PathVariable Long todoId,
            @RequestBody @Valid PlanRequestDTO.PlanUpdateDTO request) {
        Plan updatedPlan = planCommandService.updatePlan(todoId, request);
        return ApiResponse.onSuccess(PlanConverter.toPlanAddResultDTO(updatedPlan));
    }

    @GetMapping("/")
    public ApiResponse<Double> getAchievementRate() {
        return null;
    }
}
