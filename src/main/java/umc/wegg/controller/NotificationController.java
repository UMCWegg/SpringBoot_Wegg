package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.NotificationConverter;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.Notification;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.NotificationResponseDTO;
import umc.wegg.dto.TodoResponseDTO;
import umc.wegg.service.NotificationService.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id 값을 의미합니다. 항상 존재하는 것이 아니기 때문에 false
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(authenticatedUser.getUserId(), lastEventId));
    }

    @GetMapping
    public ApiResponse<List<NotificationResponseDTO.ResultDTO>> getUserNotifications(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId();
        List<Notification> notifications = notificationService.getUserNotifications(userId);

        List<NotificationResponseDTO.ResultDTO> result = notifications.stream()
                .map(NotificationConverter::toResultDTO)
                .toList();

        return ApiResponse.onSuccess(result);
    }

}
