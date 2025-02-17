package umc.wegg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;
import umc.wegg.service.PurchaseService.PurchaseCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Purchase")
public class PurchaseRestController {

    private final PurchaseCommandService purchaseCommandService;

    @GetMapping("/myPoints")
    public ApiResponse<MypointResponseDTO> getMyPoints() {
        Long userId = 1L; // 로그인 구현완료시 수정하기
        MypointResponseDTO response = purchaseCommandService.getUserPoints(userId);
        if (response != null) {
            return ApiResponse.onSuccess(response);
        } else {
            return ApiResponse.onFailure("NOT_FOUND", "사용자 정보를 찾을 수 없습니다.", null);
        }
    }
}
