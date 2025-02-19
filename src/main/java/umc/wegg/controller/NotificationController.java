package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.NotificationConverter;
import umc.wegg.domain.Notification;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.dto.*;
import umc.wegg.repository.FollowRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.NotificationService.NotificationService;
import umc.wegg.validation.annotation.ValidUser;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id 값을 의미합니다. 항상 존재하는 것이 아니기 때문에 false
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@ValidUser @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(authenticatedUser.getUserId(), lastEventId));
    }

    @GetMapping
    public ApiResponse<NotificationResponseDTO.NotificationListDTO> getUserNotifications(
            @ValidUser @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId();

        // 🔹 UserRepository 또는 UserService를 통해 Setting 가져오기
        String accountVisibility = userRepository.findAccountVisibilityByUserId(userId);

        // 🔹 로그인한 유저의 알림 리스트 가져오기
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        List<NotificationResponseDTO.ResultDTO> result = notifications.stream()
                .map(NotificationConverter::toResultDTO)
                .toList();

        // 🔹 followStatus가 WAITING인 요청 개수 조회
        Long waitingFollowRequests = followRepository.countByFolloweeIdAndFollowStatus(userId, FollowStatus.WAITING);

        // 🔹 updatedAt이 가장 최신인 follower의 accountId 조회
        String latestFollowerAccountId = followRepository.findLatestFollowerAccountIdByFolloweeId(userId).orElse(null);

        // 🔹 DTO 생성 및 응답 반환
        NotificationResponseDTO.NotificationListDTO response = NotificationResponseDTO.NotificationListDTO.builder()
                .accountVisibility(AccountVisibility.valueOf(accountVisibility))
                .notifications(result)
                .latestFollowerAccountId(latestFollowerAccountId)
                .waitingFollowRequests(waitingFollowRequests)
                .build();

        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{notification_id}")
    public ApiResponse<NotificationResponseDTO.NotificationReadDTO> readNotification(
            @PathVariable("notification_id") Long notificationId,
            @RequestBody @Valid NotificationRequestDTO.ReadDTO request) {
        Notification readNotification = notificationService.readNotification(notificationId, request);
        return ApiResponse.onSuccess(NotificationConverter.toNotificationReadDTO(readNotification));
    }

}
