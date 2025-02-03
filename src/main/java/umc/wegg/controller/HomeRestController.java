package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public ApiResponse<HomeResponseDTO.HomeWeekResponseDTO> renderWeekView() {
        HomeResponseDTO.HomeWeekResponseDTO response = homeService.getHomeWeekData();
        return ApiResponse.onSuccess(response);
    }

    // 월간 화면 렌더링
    @GetMapping("/month")
    @Operation(summary = "월간 화면 렌더링", description = "홈(월간) 화면 렌더링 API, 날짜에 따른 게시물 사진과 시간 모두 담김")
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> renderMonthView() {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getHomeMonthData();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/follow")
    @Operation(summary = "팔로우 팔로잉(+프로필 사진) 화면 렌더링", description = "홈(월간) 화면 렌더링 시 팔로우,팔로잉 API")
    public ApiResponse<HomeResponseDTO.FollowResponseDTO> renderFollowView() {
        HomeResponseDTO.FollowResponseDTO response = homeService.getHomeFollowData();
        return ApiResponse.onSuccess(response);
    }

    // 이전달/다음달 버튼 이동
    @GetMapping("/calendar/{year}/{month}")
    @Operation(summary = "이전달/다음달 버튼", description = "홈(월간) 화면에서 이전/다음 달로 이동하는 API")
    public ApiResponse<HomeResponseDTO.HomeMonthResponseDTO> getCalendarData(
            @PathVariable("year") int year,
            @PathVariable("month") int month
    ) {
        HomeResponseDTO.HomeMonthResponseDTO response = homeService.getHomeMonthDataFor(year, month);
        return ApiResponse.onSuccess(response);
    }

//    // 사진, 시간 토글 버튼
//    @GetMapping("/month/{photo_or_time}")
//    @Operation(summary = "사진, 시간 토글 버튼", description = "홈 화면에서 사진과 시간을 토글하는 API")
//    public ApiResponse<Void> togglePhotoOrTime(@PathVariable String photo_or_time) {
//        return ApiResponse.onSuccess(null);
//    }

    // 게시물 조회
//    @GetMapping("/posts/{post_id}/view")
//    @Operation(summary = "게시물 조회", description = "달력 클릭 시 해당 게시물을 조회하는 API")
//    public ApiResponse<PostResponseDTO.PostDetailResponseDTO> viewPostDetails(@PathVariable("post_id") Long postId) {
//        PostResponseDTO.PostDetailResponseDTO responseDTO = postCommandService.viewPostDetails(postId);
//        return ApiResponse.onSuccess(responseDTO);
//    }
    @PostMapping("/receive-points")
    @Operation(summary = "포인트 받기", description = "3의 배수 성공 횟수마다 포인트 지급")
    public ApiResponse<Integer> receivePoints() {
        Long userId = 1L; // 테스트용 userId
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
