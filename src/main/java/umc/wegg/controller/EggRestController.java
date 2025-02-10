package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.dto.TimeRequestDTO;
import umc.wegg.service.EggService.EggService;
import umc.wegg.service.HomeService.HomeCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eggs")
public class EggRestController {

    private final HomeCommandService homeService;

    private final EggService eggService;

    @PostMapping("/time")
    @Operation(
            summary = "시간 기록",
            description = "사용자의 공부 시간을 기록하는 API"
    )
    public ApiResponse<Void> recordTime(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody @Valid TimeRequestDTO request) {
        eggService.recordTime(authenticatedUser, request);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/calendar/plans")
    @Operation(
            summary = "캘린더 조회",
            description = "캘린더 화면에서 사용자의 계획(Plan)을 조회하는 API"
    )
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> renderMonthView(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getHomeMonthData(authenticatedUser);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{plan_id}")
    @Operation(
            summary = "알 깨기",
            description = "특정 계획(plan_id)에 연결된 알(Egg)을 깨는 API"
    )
    public ApiResponse<Void> breakEgg(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("plan_id") Long planId) {
        eggService.breakEgg(authenticatedUser, planId);
        return ApiResponse.onSuccess(null);
    }
}
