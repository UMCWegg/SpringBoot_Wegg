package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Plan;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.service.PlanService.PlanCommandService;
import umc.wegg.service.PlanService.PlanQueryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanRestController {
    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    @PostMapping("/add")
    public ApiResponse<List<PlanResponseDTO.PlanAddResultDTO>> join(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody @Valid PlanRequestDTO.PlanAddDTO request) {
        // 인증된 사용자 ID 가져오기
        Long userId = authenticatedUser.getUserId();
        // AddDTO에 userId 설정
        request.setUserId(userId);

        // 여러 날짜에 대해 계획을 추가
        List<Plan> plans = planCommandService.addPlan(request);

        // 여러 개의 계획을 PlanAddResultDTO 리스트로 변환하여 반환
        List<PlanResponseDTO.PlanAddResultDTO> result = plans.stream()
                .map(PlanConverter::toPlanAddResultDTO)
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(result);
    }


    @PatchMapping("/{plan_id}")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> updatePlan(
            @PathVariable("plan_id") Long planId,
            @RequestBody @Valid PlanRequestDTO.PlanUpdateDTO request) {
        Plan updatedPlan = planCommandService.updatePlan(planId, request);
        return ApiResponse.onSuccess(PlanConverter.toPlanAddResultDTO(updatedPlan));
    }

    @PatchMapping("/{plan_id}/onoff")
    public ApiResponse<PlanResponseDTO.PlanAddResultDTO> onoffPlan(
            @PathVariable("plan_id") Long planId,
            @RequestBody @Valid PlanRequestDTO.PlanOnoffDTO request) {
        Plan onoffPlan = planCommandService.onoffPlan(planId, request);
        return ApiResponse.onSuccess(PlanConverter.toPlanAddResultDTO(onoffPlan));
    }



    @GetMapping
    public ApiResponse<List<PlanResponseDTO.PlanDetailDTO>> getPlans(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        // 인증된 사용자 정보 가져오기
        Long userId = authenticatedUser.getUserId();

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
    public boolean checkUserLocation(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @PathVariable("plan_id") Long planId) {
        Long userId = authenticatedUser.getUserId();
        return planQueryService.isUserInPlan(planId, userId);
    }

    @DeleteMapping("/{plan_id}")
    public void deletePlan(@PathVariable Long plan_id) {
        planCommandService.deletePlan(plan_id);
    }
}
