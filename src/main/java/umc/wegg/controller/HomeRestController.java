package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.User;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.HomeService.HomeCommandService;
import umc.wegg.service.PostService.PostCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeRestController {

    private final HomeCommandService homeService;
    private final PostCommandService postCommandService;
    private final UserRepository userRepository;

    // 주간 화면 렌더링
    @GetMapping("/week")
    @Operation(summary = "주간 화면 렌더링", description = "홈(주간) 화면 렌더링 API")
    public ApiResponse<HomeResponseDTO.HomeWeekResponseDTO> renderWeekView( @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        HomeResponseDTO.HomeWeekResponseDTO response = homeService.getHomeWeekData(authenticatedUser);
        return ApiResponse.onSuccess(response);
    }

    // 월간 화면 렌더링
    @GetMapping("/month")
    @Operation(summary = "월간 화면 렌더링", description = "홈(월간) 화면 렌더링 API, 날짜에 따른 게시물 사진과 시간 모두 담김")
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> renderMonthView( @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getHomeMonthData(authenticatedUser);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/follow")
    @Operation(summary = "팔로우 팔로잉(+프로필 사진) 화면 렌더링", description = "홈(월간) 화면 렌더링 시 팔로우,팔로잉 API")
    public ApiResponse<HomeResponseDTO.FollowResponseDTO> renderFollowView( @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        HomeResponseDTO.FollowResponseDTO response = homeService.getHomeFollowData(authenticatedUser);
        return ApiResponse.onSuccess(response);
    }

    // 이전달/다음달 버튼 이동
    @GetMapping("/calendar/{year}/{month}")
    @Operation(summary = "이전달/다음달 버튼", description = "홈(월간) 화면에서 이전/다음 달로 이동하는 API")
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> getCalendarData(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("year") int year,
            @PathVariable("month") int month
    ) {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getHomeMonthDataFor(authenticatedUser, year, month);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/month/{userId}")
    @Operation(summary = "친구의 월간 화면 렌더링", description = "특정 사용자의 홈(월간) 화면을 렌더링하는 API")
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> renderFriendMonthView(@PathVariable("userId") Long userId) {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getFriendHomeMonthData(userId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/follow/{userId}")
    @Operation(summary = "친구의 팔로우/팔로잉 정보 조회", description = "특정 사용자의 팔로우 및 팔로잉 정보를 조회하는 API")
    public ApiResponse<HomeResponseDTO.FollowResponseDTO> renderFriendFollowView(
            @PathVariable("userId") Long userId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        HomeResponseDTO.FollowResponseDTO response = homeService.getFriendHomeFollowData(userId, authenticatedUser);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/calendar/{userId}/{year}/{month}")
    @Operation(summary = "친구의 이전달/다음달 버튼", description = "특정 사용자의 홈(월간) 화면에서 이전/다음 달로 이동하는 API")
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> getFriendCalendarData(
            @PathVariable("userId") Long userId,
            @PathVariable("year") int year,
            @PathVariable("month") int month
    ) {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getFriendHomeMonthDataFor(userId, year, month);
        return ApiResponse.onSuccess(response);
    }



    @PostMapping("/receive-points")
    @Operation(summary = "포인트 받기", description = "3의 배수 성공 횟수마다 포인트 지급")
    public ApiResponse<Integer> receivePoints( @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int successCount = user.getSuccessCount();

        // 가장 최근에 포인트를 받은 successCount 조회
        int lastReceivedSuccessCount = user.getLastReceivedSuccessCount();

        int pointsToReceive = 0;

        // 현재 successCount까지 중에서 3의 배수이고, 아직 받지 않은 포인트를 계산
        for (int i = lastReceivedSuccessCount + 3; i <= successCount; i += 3) {
            pointsToReceive += 3;
        }

        if (pointsToReceive > 0) {
            // 📌 포인트 지급
            user.setPoints(user.getPoints() + pointsToReceive);

            // 📌 마지막 받은 successCount 업데이트
            user.setLastReceivedSuccessCount(successCount);
            userRepository.save(user); // 변경 사항 저장

            return ApiResponse.onSuccess(pointsToReceive);
        } else {
            return null;
            //ApiResponse.onError("받을 수 있는 포인트가 없습니다.");
        }
    }


}
