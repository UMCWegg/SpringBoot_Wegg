package umc.wegg.service.NotificationService;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.NotificationType;

public interface NotificationService {
    SseEmitter subscribe(Long memberId, String lastEventId);
    void send(User receiver, NotificationType notificationType, String content, String url);
    void sendNotificationToPostOwner(User postOwner, Long postId, String message, String notificationType);
    void sendNotificationToEggOwner(User planUser, String content, String notificationType);
}
