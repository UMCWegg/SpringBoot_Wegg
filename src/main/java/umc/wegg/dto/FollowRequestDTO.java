package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FollowRequestDTO {

    // 1. 팔로우 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFollowRequestDTO {
        private Long followerId; // 팔로우 요청을 보낸 사용자 ID
        private Long followeeId; // 팔로우 요청을 받은 사용자 ID
    }

    // 2. 팔로우 요청 수락/거절 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecideFollowRequestDTO {
        private Long followId; // 팔로우 요청 ID (필수)
        private String followSucceed; // 팔로우 요청 처리 상태 ("SUCCEEDED", "REJECTED", "WAITING")
    }
}

