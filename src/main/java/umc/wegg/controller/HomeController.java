package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.service.HomeService.HomeCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeCommandService homeService;

    // 주간 화면 렌더링
    @GetMapping("/week")
    @Operation(summary = "주간 화면 렌더링", description = "홈(주간) 화면 렌더링 API")
    public ApiResponse<HomeResponseDTO> renderWeekView() {
        HomeResponseDTO response = homeService.getHomeWeekData();
        return ApiResponse.onSuccess(response);
    }

    // 월간 화면 렌더링
    @GetMapping("/month")
    @Operation(summary = "월간 화면 렌더링", description = "홈(월간) 화면 렌더링 API")
    public ApiResponse<HomeResponseDTO> renderMonthView() {
        HomeResponseDTO response = homeService.getHomeMonthData();
        return ApiResponse.onSuccess(response);
    }

    // 이전달/다음달 버튼 이동
    @GetMapping("/calendar/{year}/{month}/{photo_or_time}")
    @Operation(summary = "이전달/다음달 버튼", description = "홈(월간) 화면에서 이전/다음 달로 이동하는 API")
    public ApiResponse<Void> handleCalendarNavigation(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String photo_or_time
    ) {
        return ApiResponse.onSuccess(null);
    }

    // 사진, 시간 토글 버튼
    @GetMapping("/month/{photo_or_time}")
    @Operation(summary = "사진, 시간 토글 버튼", description = "홈 화면에서 사진과 시간을 토글하는 API")
    public ApiResponse<Void> togglePhotoOrTime(@PathVariable String photo_or_time) {
        return ApiResponse.onSuccess(null);
    }

    // 게시물 조회
    @GetMapping("/posts/{post_id}/view")
    @Operation(summary = "게시물 조회", description = "달력 클릭 시 해당 게시물을 조회하는 API")
    public ApiResponse<Void> viewPost(@PathVariable Long post_id) {
        return ApiResponse.onSuccess(null);
    }
}
