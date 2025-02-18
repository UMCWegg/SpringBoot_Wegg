package umc.wegg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PurchaseRequestDTO;
import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;
import umc.wegg.service.PurchaseService.PurchaseCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Purchase")
public class PurchaseRestController {

    private final PurchaseCommandService purchaseCommandService;

    @GetMapping("/myPoints")
    public ApiResponse<MypointResponseDTO> getMyPoints(AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID

        MypointResponseDTO response = purchaseCommandService.getUserPoints(userId);
        if (response != null) {
            return ApiResponse.onSuccess(response);
        } else {
            return ApiResponse.onFailure("NOT_FOUND", "사용자 정보를 찾을 수 없습니다.", null);
        }
    }

    @PostMapping("/template")
    public ApiResponse<String> purchaseTemplate(AuthenticatedUser authenticatedUser, @RequestBody PurchaseRequestDTO.TemplatePurchaseRequestDTO requestDTO) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID

        boolean success = purchaseCommandService.purchaseTemplate(userId, requestDTO.getTemplateType());
        if (success) {
            return ApiResponse.onSuccess("템플릿 구매가 완료되었습니다.");
        } else {
            return ApiResponse.onFailure("INSUFFICIENT_FUNDS", "포인트가 부족합니다.", null);
        }
    }

    @PostMapping("/addPoints")
    public ApiResponse<String> addPoints(AuthenticatedUser authenticatedUser, @RequestBody PurchaseRequestDTO.AddPointsRequestDTO requestDTO) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID

        boolean success = purchaseCommandService.addPoints(userId, requestDTO.getPointsToAdd());
        if (success) {
            return ApiResponse.onSuccess("포인트 충전 완료");
        } else {
            return ApiResponse.onFailure("INVALID_AMOUNT", "올바르지 않은 포인트 값입니다.", null);
        }
    }

}
