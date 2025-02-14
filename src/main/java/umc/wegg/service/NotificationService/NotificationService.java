package umc.wegg.service.NotificationService;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.domain.Notification;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.dto.NotificationRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    SseEmitter subscribe(Long userId, String lastEventId);
    void send(User receiver, NotificationType notificationType, String content, String url, String profileImage);
    void sendNotificationToPostOwner(User postOwner, Long postId, String message, String notificationType, String profileImage);
    void sendNotificationToEggOwner(User planUser, String content, String notificationType, String profileImage);
    void scheduleNotification(User user, NotificationType type, LocalDateTime notificationTime, String content, String url, String profileImage);
    List<Notification> getUserNotifications(Long userId);
    Notification readNotification(Long notificationId, NotificationRequestDTO.ReadDTO request);
    void sendNotificationToFollower(User follower, Long postId, String message, String notificationType, String profileImage);
    void scheduleTask(Runnable task, LocalDateTime executionTime);
}
