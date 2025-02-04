package umc.wegg.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.domain.Notification;
import umc.wegg.domain.Plan;
import umc.wegg.domain.User;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.domain.enums.ReadStatus;
import umc.wegg.repository.EmitterRepository;
import umc.wegg.repository.NotificationRepository;
import umc.wegg.repository.PlanRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public void sendNotificationToPostOwner(User postOwner, Long postId, String content, String notificationType) {
        NotificationType type = NotificationType.valueOf(notificationType.toUpperCase());
        send(postOwner, type, content, "/posts/" + postId + "/view");
    }

    public void sendNotificationToEggOwner(User planUser, String content, String notificationType) {
        NotificationType type = NotificationType.valueOf(notificationType.toUpperCase());
        send(planUser, type, content, "/eggs/calender/plans");
    }

//    @Scheduled(fixedRate = 60000) // 1분마다 실행
//    public void checkPlansAndNotify() {
//        List<Plan> plans = planRepository.findAll(); // 모든 계획 가져오기
//        long currentTime = System.currentTimeMillis();
//
//        System.out.println("Checking plans...");  // 디버깅용 로그
//
//        for (Plan plan : plans) {
//            LocalDateTime startTime = plan.getStartTime(); // LocalDateTime 타입
//            LocalDateTime finishTime = plan.getFinishTime(); // LocalDateTime 타입 (추가)
//
//            if (startTime != null && finishTime != null) {
//                // LocalDateTime을 ZonedDateTime으로 변환 (기본적으로 시스템 시간대 사용)
//                long planStartTime = startTime.atZone(ZoneId.systemDefault())
//                        .toInstant()
//                        .toEpochMilli(); // 밀리초 단위로 변환
//                long planEndTime = finishTime.atZone(ZoneId.systemDefault())
//                        .toInstant()
//                        .toEpochMilli(); // 밀리초 단위로 변환
//
//                // 계획이 10분 후에 예정된 경우 (현재 시간보다 10분 전인 경우)
//                if (planStartTime - currentTime <= 10 * 60 * 1000 && planStartTime - currentTime > 9 * 60 * 1000) {
//                    // 알림 전송
//                    User user = plan.getUser();
//                    if (user != null) {
//                        send(user, NotificationType.PLAN_REMINDER, "장소를 인증하고 공부를 시작하는데에 10분 남았습니다.", "/plans/" + plan.getId());
//                    }
//                }
//
//                // 계획의 시작 시간이 되면 "공부를 시작하세요!" 알림
//                if (planStartTime <= currentTime && planStartTime + 60000 > currentTime) {
//                    // 알림 전송
//                    User user = plan.getUser();
//                    if (user != null) {
//                        send(user, NotificationType.PLACE_VERIFY, "시간이 다 되었습니다! 인증을 진행해주세요.", "/plans/" + plan.getId()+"/check");
//                    }
//                }
//
//                // startTime과 endTime 사이에 랜덤 시간으로 장소 인증 알림 보내기
//                if (currentTime >= planStartTime && currentTime <= planEndTime) {
//                    Random rand = new Random();
//                    long randomDelay = planStartTime + rand.nextInt((int) (planEndTime - planStartTime)); // startTime과 endTime 사이의 랜덤 시간
//                    if (currentTime >= randomDelay && currentTime < randomDelay + 60000) { // 1분 이내에 알림
//                        // 장소 인증 알림 전송
//                        User user = plan.getUser();
//                        if (user != null) {
//                            send(user, NotificationType.RANDOM_VERIFY, "장소 인증을 완료하고 공부 사진을 찍어보세요!", "/post");
//                        }
//                    }
//                }
//            }
//        }
//    }




}
