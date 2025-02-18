package umc.wegg.service.FollowService;

import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.dto.FollowResponseDTO;

import java.util.List;

public interface FollowCommandService {
    FollowStatus createFollowRequest(FollowRequestDTO.CreateFollowRequestDTO requestDTO);
    void decideFollowRequest(FollowRequestDTO.DecideFollowRequestDTO requestDTO, FollowStatus followStatus);


    List<FollowResponseDTO.UserRecommendationDTO> getFollowRequests(Long myId);

    // 연락처에 있는 사용자 추천
    List<FollowResponseDTO.UserRecommendationDTO> getContactRecommendations(Long myId);

    // 회원님을 위한 사용자 추천 (팔로워 기반 추천)
    List<FollowResponseDTO.UserRecommendationDTO> getGeneralRecommendations(Long myId);
}

