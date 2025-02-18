package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.domain.enums.ReadStatus;
import umc.wegg.domain.enums.TodoListStatus;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListDTO {
        AccountVisibility accountVisibility; // 🔹 로그인한 유저의 accountVisibility 추가
        String latestFollowerAccountId;
        Long waitingFollowRequests;
        List<ResultDTO> notifications; // 🔹 기존의 알림 리스트

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDTO{
        Long notificationId;
        NotificationType notificationType;
        String content;
        String url;
        ReadStatus readStatus;
        String imageUrl;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationReadDTO {
        Long notificationId;
        ReadStatus readStatus;
    }
}
