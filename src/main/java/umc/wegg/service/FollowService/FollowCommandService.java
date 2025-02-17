package umc.wegg.service.FollowService;

import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.dto.FollowResponseDTO;

import java.util.List;

public interface FollowCommandService {
    FollowStatus createFollowRequest(FollowRequestDTO.CreateFollowRequestDTO requestDTO);
    void decideFollowRequest(FollowRequestDTO.DecideFollowRequestDTO requestDTO, FollowStatus followStatus);
    // 나에게 온 팔로우 요청들
    List<FollowResponseDTO.UserRecommendationDTO> getFollowRequests(Long myId);

    // 연락처에 있는 사용자 추천
    List<FollowResponseDTO.UserRecommendationDTO> getContactRecommendations(Long myId);

}

