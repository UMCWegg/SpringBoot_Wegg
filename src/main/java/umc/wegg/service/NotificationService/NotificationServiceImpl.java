package umc.wegg.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.domain.Notification;
import umc.wegg.domain.User;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.domain.enums.ReadStatus;
import umc.wegg.repository.EmitterRepository;
import umc.wegg.repository.NotificationRepository;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1 hour

    @Override
    public SseEmitter subscribe(Long memberId, String lastEventId) {
        // 고유 ID 생성
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // SSE 연결이 완료되거나 타임아웃되면 해당 emitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 클라이언트에게 더미 이벤트 전송
        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        // 이전에 읽지 않은 이벤트가 있다면 전송
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    @Override
    @Transactional
    public void send(User receiver, NotificationType notificationType, String content, String url) {
        Notification notification = createNotification(receiver, notificationType, content, url);
        notificationRepository.save(notification);

        String memberId = String.valueOf(receiver.getId());

        // SSE 연결된 모든 클라이언트에게 알림 전송
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            // ApiResponse<Notification> 사용
            sendToClient(emitter, key, ApiResponse.onSuccess(notification));
        });
    }

    // Notification 객체 생성
    private Notification createNotification(User user, NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .content(content)
                .url(url)
                .readStatus(ReadStatus.UNREAD)
                .build();
    }

    // 클라이언트로 데이터 전송
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("Failed to send notification");
        }
    }
}
