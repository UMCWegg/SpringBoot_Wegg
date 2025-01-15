package umc.wegg.service.FollowService;

import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.dto.FollowRequestDTO;

public interface FollowCommandService {
    void createFollowRequest(FollowRequestDTO.CreateFollowRequestDTO requestDTO);
    void decideFollowRequest(FollowRequestDTO.DecideFollowRequestDTO requestDTO, FollowStatus followStatus);
}

