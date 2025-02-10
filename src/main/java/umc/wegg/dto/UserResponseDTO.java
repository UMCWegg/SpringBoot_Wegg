package umc.wegg.dto;

import lombok.*;
import umc.wegg.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UserResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJoinResultDTO{
        Long userId;
        LocalDateTime createdAt;
        List<ContactFriendDto> contactFriends;

        //연락처에 있는 user
        @Getter
        @AllArgsConstructor
        public static class ContactFriendDto {
            private Long friendId;
            private String accountId;
            private String name;
            private String profileImage;
            private String phone;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2UserJoinResultDTO{
        Long userId;
        LocalDateTime createdAt;
        List<ContactFriendDto> contactFriends;

        //연락처에 있는 user
        @Getter
        @AllArgsConstructor
        public static class ContactFriendDto {
            private Long friendId;
            private String accountId;
            private String name;
            private String profileImage;
            private String phone;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ContactFriendDTO {
        private User friend;
        private String accountId;
        private String name;
        private String profileImage;
        private String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDTO {
        private boolean success; // 성공 여부
        private Long userId; // 사용자 ID
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2LoginResultDTO {
        private boolean success; // 성공 여부
        private String provider;
        private String oauthId; // 사용자 ID
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDeleteResultDTO {
        private boolean success; // 성공 여부
        private Long userId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateResultDTO {
        private boolean success; // 성공 여부
        private Map<String, Object> updatedFields; // 수정된 필드 (key: 필드명, value: 수정된 값)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationResultDTO {
        private String message; // 예: "인증번호가 전송되었습니다."
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyNumberResultDTO {
        private boolean isValid; // 인증번호 일치 여부
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckAccountIdResultDTO {
        private boolean isDuplicate;
        private String message;  // 결과 메시지
    }

    @Data
    @AllArgsConstructor
    public static class UserSearchDTO {
        private Long userId;
        private String accountId;
        private String profileImage;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactUpdateResultDTO {
        private boolean success;
    }
}
