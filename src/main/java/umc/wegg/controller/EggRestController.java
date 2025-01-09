package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.EggRequestDTO;
import umc.wegg.dto.TimeRequestDTO;
import umc.wegg.service.EggService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eggs")
public class EggRestController {

    private final EggService eggService;

    @PostMapping("/time")
    @Operation(
            summary = "시간 기록",
            description = "사용자의 공부 시간을 기록하는 API"
    )
    public ApiResponse<Void> recordTime(@RequestBody @Valid TimeRequestDTO request) {
        eggService.recordTime(request);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/calendar/plans")
    @Operation(
            summary = "캘린더 조회",
            description = "캘린더 화면에서 사용자의 계획(Plan)을 조회하는 API"
    )
    public ApiResponse<Object> getCalendarPlans() {
        return ApiResponse.onSuccess(eggService.getCalendarPlans());
    }

    @PatchMapping("/{plan_id}")
    @Operation(
            summary = "알 깨기",
            description = "특정 계획(plan_id)에 연결된 알(Egg)을 깨는 API"
    )
    public ApiResponse<Void> breakEgg(
            @PathVariable("plan_id") Long planId,
            @RequestBody @Valid EggRequestDTO request) {
        eggService.breakEgg(planId, request);
        return ApiResponse.onSuccess(null);
    }
}
