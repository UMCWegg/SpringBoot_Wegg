package umc.wegg.converter;

import umc.wegg.domain.Notification;
import umc.wegg.dto.NotificationResponseDTO;

public class NotificationConverter {

    // Notification 객체를 ResultDTO로 변환하는 메서드
    public static NotificationResponseDTO.ResultDTO toResultDTO(Notification notification) {
        return NotificationResponseDTO.ResultDTO.builder()
                .notificationId(notification.getId()) // Notification의 ID
                .notificationType(notification.getNotificationType()) // NotificationType (예: 알림 종류)
                .content(notification.getContent()) // 알림 내용
                .url(notification.getUrl()) // 알림 URL (예: 알림을 클릭했을 때 이동할 URL)
                .readStatus(notification.getReadStatus()) // 알림 읽음 여부
                .build();
    }
    public static NotificationResponseDTO.NotificationReadDTO toNotificationReadDTO(Notification notification) {
        return NotificationResponseDTO.NotificationReadDTO.builder()
                .notificationId(notification.getId())
                .readStatus(notification.getReadStatus())
                .build();
    }
}
