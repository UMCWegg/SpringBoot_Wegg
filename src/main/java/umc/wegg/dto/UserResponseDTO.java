package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJoinResultDTO{
        Long userId;
        LocalDateTime createdAt;
        List<ExistingUserDTO> existingUsers;

        //연락처에 있는 user
        @Getter
        @AllArgsConstructor
        public static class ExistingUserDTO {
            private String contactName;
            private String name;
            private String phone;
        }
    }
}
