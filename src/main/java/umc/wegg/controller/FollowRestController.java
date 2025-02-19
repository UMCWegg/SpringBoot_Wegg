package umc.wegg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.dto.FollowResponseDTO;
import umc.wegg.repository.FollowRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.FollowService.FollowCommandService;
import umc.wegg.domain.apiPayload.ApiResponse;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowRestController {

    private final FollowCommandService followCommandService;
    private final UserRepository userRepository;


    /**
     * 팔로우 요청 생성
     * @param requestDTO 팔로우 요청 데이터
     * @return 응답 메시지
     */
    @PostMapping
    public ApiResponse<FollowResponseDTO.CreateFollowResponseDTO> createFollowRequest(
            @RequestBody FollowRequestDTO.CreateFollowRequestDTO requestDTO) {

        FollowStatus followStatus = followCommandService.createFollowRequest(requestDTO);

        String message = (followStatus == FollowStatus.SUCCEEDED)
                ? "공개 계정을 팔로우 했습니다."
                : "비공개 계정 팔로우 요청이 완료되었습니다.";

        return ApiResponse.onSuccess(
                new FollowResponseDTO.CreateFollowResponseDTO(message, followStatus));
    }

    /**
     * 공개 계정의 팔로우 취소 (즉시 삭제)
     * @param requestDTO 팔로우 취소 요청 데이터
     * @return 응답 메시지
     */
    @DeleteMapping
    public ApiResponse<FollowResponseDTO.DeleteFollowResponseDTO> deleteFollowRequest(
            @RequestBody FollowRequestDTO.DeleteFollowRequestDTO requestDTO) {

        boolean isDeleted = followCommandService.deleteFollowRequest(requestDTO);

        String message = isDeleted ? "팔로우가 취소되었습니다." : "비공개 계정의 팔로우 요청은 취소만 가능합니다.";

        return ApiResponse.onSuccess(new FollowResponseDTO.DeleteFollowResponseDTO(message));
    }


    /**
     * 팔로우 요청 수락
     * @param requestDTO 팔로우 요청 처리 데이터
     * @return 응답 메시지
     */
    @PatchMapping("/accept")
    public ApiResponse<FollowResponseDTO.AcceptFollowResponseDTO> acceptFollowRequest(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody FollowRequestDTO.DecideFollowRequestDTO requestDTO) {

        // FollowStatus.SUCCEEDED를 추가하여 호출
        followCommandService.decideFollowRequest(requestDTO, FollowStatus.SUCCEEDED);

        return ApiResponse.onSuccess(
                new FollowResponseDTO.AcceptFollowResponseDTO("Follow request updated successfully."));
    }

    /**
     * 팔로우 요청 거절하기
     * @param requestDTO 팔로우 요청 처리 데이터
     * @return 응답 메시지
     */
    @PatchMapping("/reject")
    public ApiResponse<FollowResponseDTO.RejectFollowResponseDTO> rejectFollowRequest(
            @RequestBody FollowRequestDTO.DecideFollowRequestDTO requestDTO) {
        followCommandService.decideFollowRequest(requestDTO, FollowStatus.REJECTED);
        return ApiResponse.onSuccess(
                new FollowResponseDTO.RejectFollowResponseDTO("Follow request rejected successfully."));
    }

    @GetMapping("/recommendations")
    public ApiResponse<Map<String, Object>> getFriendRecommendations(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Map<String, Object> recommendations = new LinkedHashMap<>(); // 순서 유지하려고 LinkedHashMap 씀

        // 인증된 사용자 ID 가져오기
        Long myId = authenticatedUser.getUserId();

        // 사용자 조회
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + myId));

        // 내 계정 공개 여부 확인
        AccountVisibility accountVisibility = (user.getSetting() != null)
                ? user.getSetting().getAccountVisibility()
                : AccountVisibility.PUBLIC; // 기본값: 공개 계정

        // AccountVisibility 추가
        recommendations.put("accountVisibility", accountVisibility); // 첫 번째로 추가

        // 나에게 온 팔로우 요청 리스트 조회
        List<FollowResponseDTO.UserRecommendationDTO> followRequests = followCommandService.getFollowRequests(myId);
        recommendations.put("followRequests", followRequests); // 두 번째로 추가

        // 연락처 기반 추천
        List<FollowResponseDTO.UserRecommendationDTO> contactRecommendations = followCommandService.getContactRecommendations(myId);
        recommendations.put("contactRecommendations", contactRecommendations); // 세 번째로 추가

        // 회원님을 위한 추천
        List<FollowResponseDTO.UserRecommendationDTO> generalRecommendations = followCommandService.getGeneralRecommendations(myId);
        recommendations.put("generalRecommendations", generalRecommendations); // 네 번째로 추가

        return ApiResponse.onSuccess(recommendations);
    }





}

