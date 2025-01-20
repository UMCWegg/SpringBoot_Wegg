package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Plan;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.service.PlanService.PlanCommandService;
import umc.wegg.service.PlanService.PlanQueryService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanRestController {
    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    @PostMapping("/add")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> join(@RequestBody @Valid PlanRequestDTO.PlanAddDTO request){
    // 인증된 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getId(); // 인증된 사용자 ID
        Long userId = 2L;
        // AddDTO에 userId 설정
        request.setUserId(userId);
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
    public ApiResponse<List<PlanResponseDTO.PlanDetailDTO>> getPlans() {
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 2L;  // 인증된 사용자 ID 가져오기

        // 인증된 사용자의 계획만 가져오기
        List<Plan> plans = planQueryService.getPlansByUserId(userId);

        // DTO로 변환하여 응답
        List<PlanResponseDTO.PlanDetailDTO> response = plans.stream()
                .map(PlanConverter::toPlanDetailDTO)
                .toList();

        return ApiResponse.onSuccess(response);
    }


    // 사용자가 계획에 설정된 가게 내에 있는지 확인
    @GetMapping("/{plan_id}/check")
    public boolean checkUserLocation(@PathVariable("plan_id") Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 2L;
        return planQueryService.isUserInPlan(planId, userId);
    }

    @DeleteMapping("/{plan_id}")
    public void deletePlan(@PathVariable Long plan_id) {
        planCommandService.deletePlan(plan_id);
    }
}
