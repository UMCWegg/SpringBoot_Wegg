package umc.wegg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.dto.FollowResponseDTO;
import umc.wegg.service.FollowService.FollowCommandService;
import umc.wegg.domain.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowRestController {

    private final FollowCommandService followCommandService;

    /**
     * 팔로우 요청 생성
     * @param requestDTO 팔로우 요청 데이터
     * @return 응답 메시지
     */
    @PostMapping
    public ApiResponse<FollowResponseDTO.CreateFollowResponseDTO> createFollowRequest(
            @RequestBody FollowRequestDTO.CreateFollowRequestDTO requestDTO) {
        followCommandService.createFollowRequest(requestDTO);
        return ApiResponse.onSuccess(
                new FollowResponseDTO.CreateFollowResponseDTO("Follow request sent successfully."));
    }

    /**
     * 팔로우 요청 수락
     * @param requestDTO 팔로우 요청 처리 데이터
     * @return 응답 메시지
     */
    @PatchMapping
    public ApiResponse<FollowResponseDTO.AcceptFollowResponseDTO> acceptFollowRequest(
            @RequestBody FollowRequestDTO.DecideFollowRequestDTO requestDTO) {
        followCommandService.decideFollowRequest(requestDTO, FollowStatus.SUCCEEDED);
        return ApiResponse.onSuccess(
                new FollowResponseDTO.AcceptFollowResponseDTO("Follow request accepted successfully."));
    }

    /**
     * 팔로우 요청 거절
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
}

