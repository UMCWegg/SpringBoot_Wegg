package umc.wegg.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.domain.Notification;
import umc.wegg.domain.Plan;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.User;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.domain.enums.ReadStatus;
import umc.wegg.dto.NotificationRequestDTO;
import umc.wegg.dto.TodoRequestDTO;
import umc.wegg.repository.EmitterRepository;
import umc.wegg.repository.NotificationRepository;
import umc.wegg.repository.PlanRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final PlanRepository planRepository;

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
        System.out.println("Sending notification to " + sseEmitters.size() + " subscribers.");
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
            System.out.println("[DEBUG] Sending event to emitterId: " + emitterId);
            emitter.send(SseEmitter.event().id(emitterId).data(data));
            System.out.println("[DEBUG] Event sent successfully: " + data);
        } catch (IOException exception) {
            System.out.println("[ERROR] Failed to send event to emitterId: " + emitterId);
            emitterRepository.deleteById(emitterId);
            exception.printStackTrace();  // 예외 출력
        }
    }


    public void sendNotificationToPostOwner(User postOwner, Long postId, String content, String notificationType) {
        NotificationType type = NotificationType.valueOf(notificationType.toUpperCase());
        send(postOwner, type, content, "/posts/" + postId + "/view");
    }

    public void sendNotificationToEggOwner(User planUser, String content, String notificationType) {
        NotificationType type = NotificationType.valueOf(notificationType.toUpperCase());
        send(planUser, type, content, "/eggs/calender/plans");
    }


    public void scheduleNotification(User user, NotificationType type, LocalDateTime notificationTime, String content, String url) {
        long delay = notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();

        // 일정 시간 후에 알림을 전송하는 작업을 예약합니다.
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            send(user, type, content, url);
        }, delay, TimeUnit.MILLISECONDS);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    public Notification readNotification(Long notificationId, NotificationRequestDTO.ReadDTO request) {
        Notification existingNoti = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (request.getReadStatus() != null) {
            existingNoti.setReadStatus(request.getReadStatus());
        }

        return notificationRepository.save(existingNoti);

    }
}
