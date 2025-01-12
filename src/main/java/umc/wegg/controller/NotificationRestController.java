package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.domain.Notification;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.service.NotificationService.NotificationCommandService;
import umc.wegg.dto.NotificationRequestDTO;
import umc.wegg.dto.NotificationResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationRestController {

    private final NotificationCommandService notificationCommandService;


    @GetMapping("/")
    public ApiResponse<List<NotificationResponseDTO>> getNotifications() {
//        List<Notification> notifications = notificationCommandService.getAllNotifications();
//        List<NotificationResponseDTO> response = notifications.stream()
//                .map(NotificationConverter::toNotificationResponseDTO)
//                .collect(Collectors.toList());
//        return ApiResponse.onSuccess(response);
        return null;
    }

    @PatchMapping("/{notification_id}")
    public ApiResponse<NotificationResponseDTO> updateNotification(
            @PathVariable Long notification_id,
            @RequestBody @Valid NotificationRequestDTO.ReadDTO request) {
//        Notification updatedNotification = notificationCommandService.updateNotification(notification_id, request);
//        NotificationResponseDTO response = NotificationConverter.toNotificationResponseDTO(updatedNotification);
//        return ApiResponse.onSuccess(response);
        return null;
    }

    @GetMapping("/{notification_id}/redirect")
    public ApiResponse<Void> redirectNotification(@PathVariable Long notification_id) {
//        notificationCommandService.redirectNotification(notification_id);
//        return ApiResponse.onSuccess(null);
        return null;
    }
}
