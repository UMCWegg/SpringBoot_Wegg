package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.MypageRequestDTO;
import umc.wegg.service.MypageService.MypageCommandService;
import umc.wegg.validation.annotation.ValidUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageRestController {

    private final MypageCommandService mypageCommandService;

    @PatchMapping("/setting")
    public ApiResponse<String> updateSettings(@ValidUser @AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody @Valid MypageRequestDTO.SettingDTO request) {
        Long userId = authenticatedUser.getUserId(); // 예: 인증된 사용자 ID 가져오기 (실제로는 SecurityContext 등을 사용해야 함)
        mypageCommandService.updateSettings(userId, request);
        return ApiResponse.onSuccess("Settings updated successfully");
    }

}