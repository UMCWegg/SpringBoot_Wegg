package umc.wegg.controller;

import com.amazonaws.services.ec2.model.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.User;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PurchaseRequestDTO;
import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.PurchaseService.PurchaseCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Purchase")
public class PurchaseRestController {

    private final PurchaseCommandService purchaseCommandService;
    private final UserRepository userRepository;

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

    @PostMapping("/purchase-points")
    public ApiResponse<Integer> purchasePoints(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                               @RequestBody PurchaseRequestDTO request) {
        Long userId = authenticatedUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int purchaseAmount = request.getPoints(); // 프론트에서 보낸 포인트 값

        if (purchaseAmount <= 0) {
            return ApiResponse.onFailure("INVALID_AMOUNT", "유효한 포인트 값을 입력해주세요.", null);
        }

        user.setPoints(user.getPoints() + purchaseAmount);
        userRepository.save(user);

        return ApiResponse.onSuccess(user.getPoints());
    }
}
