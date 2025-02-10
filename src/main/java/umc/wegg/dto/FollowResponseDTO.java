package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FollowResponseDTO {

    // 1. 팔로우 요청에 대한 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFollowResponseDTO {
        private String message; // 응답 메시지 (예: "Follow request sent successfully.")
    }

    // 2. 팔로우 요청 수락에 대한 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptFollowResponseDTO {
        private String message; // 응답 메시지 (예: "Follow request accepted successfully.")
    }

    // 3. 팔로우 요청 거절에 대한 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectFollowResponseDTO {
        private String message; // 응답 메시지 (예: "Follow request rejected successfully.")
    }

    // 4. 친구추천화면에서 쓰이는 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRecommendationDTO {
        private Long userId;
        private String profileImage;
        private String accountId;
        private String userName;
    }
}

