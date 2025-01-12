package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.service.SmsService.SmsService;
import umc.wegg.service.UserService.UserCommandService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserRestController {

    private final UserCommandService userCommandService;
    private final SmsService smsService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입",description = "회원가입 API")
    public ApiResponse<UserResponseDTO.UserJoinResultDTO> join(@RequestBody @Valid UserRequestDTO.UserJoinDto request){
//        User user = userCommandService.joinUser(request);
        UserResponseDTO.UserJoinResultDTO response = userCommandService.joinUser(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/phone/send-verification")
    @Operation(summary = "인증번호 전송(전화번호)",description = "사용자의 전화번호로 인증번호를 전송하는 API")
    public ApiResponse<UserResponseDTO.VerificationResultDTO> sendPhoneVerificationCode(@RequestBody @Valid UserRequestDTO.SendPhoneVerificationDto request){

        String message = smsService.sendSms(request); // 서비스 계층에서 메시지 생성 및 처리
        UserResponseDTO.VerificationResultDTO response = new UserResponseDTO.VerificationResultDTO(message);

        return ApiResponse.onSuccess(response);
    }
    @GetMapping("/id-check")
    @Operation(summary = "아이디 중복 체크", description = "아이디의 중복 여부를 확인하는 API. 중복일 경우 result 값 true")
    public ApiResponse<Boolean> checkAccountIdDuplication(
            @RequestParam("accountId") String accountId) {
        boolean isDuplicate = userCommandService.checkAccountIdDuplication(accountId);
        return ApiResponse.onSuccess(isDuplicate);
    }
}
