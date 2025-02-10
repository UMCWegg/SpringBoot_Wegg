package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.service.MailService.MailService;
import umc.wegg.service.MapService.MapServiceImpl;
import umc.wegg.service.SmsService.SmsService;
import umc.wegg.service.UserService.UserCommandService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserRestController {

    private final UserCommandService userCommandService;
    private final SmsService smsService;
    private final MailService mailService;
    private final MapServiceImpl mapServiceImpl;

    @PostMapping("/signup")
    @Operation(summary = "회원가입",description = "회원가입 API")
    public ApiResponse<UserResponseDTO.UserJoinResultDTO> join(@RequestBody @Valid UserRequestDTO.UserJoinDto request){
        UserResponseDTO.UserJoinResultDTO response = userCommandService.joinUser(request);
        return ApiResponse.onSuccess(response);
    }

    //확인용 api
    @GetMapping("/info")
    public void infoMember(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        log.info("authenticatedUser -> {}", authenticatedUser.toString());
    }

    @PostMapping("/oauth2/signup")
    @Operation(summary = "OAuth 회원가입", description = "OAuth 인증을 완료하고 사용자 정보를 저장하는 API")
    public ApiResponse<?> oAuth2Signup(@RequestBody @Valid UserRequestDTO.OAuth2UserJoinDto request) {

        UserResponseDTO.OAuth2UserJoinResultDTO response = userCommandService.oAuth2JoinUser(request);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/oauth2/login")
    @Operation(summary = "OAuth 로그인", description = "OAuth 인증을 완료하고 OAuth 사용자가 회원가입을 했는지 확인하는 API")
    public ApiResponse<?> oAuth2Login(HttpServletRequest request, @AuthenticationPrincipal OAuth2User oauth2User) {

        UserResponseDTO.OAuth2LoginResultDTO response = userCommandService.oAuth2LoginUser(request, oauth2User);

        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/resign")
    @Operation(summary = "회원 탈퇴",description = "회원 탈퇴 API. ")
    public ApiResponse<UserResponseDTO.UserDeleteResultDTO> deleteUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        UserResponseDTO.UserDeleteResultDTO response = userCommandService.deleteUser(authenticatedUser);

        return ApiResponse.onSuccess(response);
    }

    @PatchMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "회원정보 수정", description = "회원정보 수정 API")
    public ApiResponse<UserResponseDTO.UserUpdateResultDTO> updateUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestPart(value = "request", required = false) @Valid UserRequestDTO.UserUpdateDto request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profilePicture
    ) throws IOException {
        // 회원 정보 업데이트 서비스 호출
        UserResponseDTO.UserUpdateResultDTO response = userCommandService.updateUser(authenticatedUser, request, profilePicture);

        return ApiResponse.onSuccess(response);
    }


    @PostMapping("/phone/send-verification")
    @Operation(summary = "인증번호 전송(전화번호)",description = "사용자의 전화번호로 인증번호를 전송하는 API")
    public ApiResponse<UserResponseDTO.VerificationResultDTO> sendPhoneVerificationCode(@RequestBody @Valid UserRequestDTO.SendPhoneVerificationDto request){

        String message = smsService.sendSms(request); // 메시지 생성 및 처리
        UserResponseDTO.VerificationResultDTO response = new UserResponseDTO.VerificationResultDTO(message);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/email/send-verification")
    @Operation(summary = "인증번호 전송(이메일)",description = "사용자의 이메일로 인증번호를 전송하는 API")
    public ApiResponse<UserResponseDTO.VerificationResultDTO> sendEmailVerificationCode(@RequestBody @Valid UserRequestDTO.SendEmailVerificationDto request){

        UserResponseDTO.VerificationResultDTO response = mailService.sendMail(request);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/verifinum-check")
    @Operation(summary = "인증번호 확인",description = "인증번호가 일치하는지 확인하는 API. 일치할 경우 result 값 true")
    public ApiResponse<UserResponseDTO.VerifyNumberResultDTO> verifyNumber(@RequestBody @Valid UserRequestDTO.VerifyNumberDto request){

        UserResponseDTO.VerifyNumberResultDTO response = userCommandService.verityNumber(request);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/id-check")
    @Operation(summary = "아이디 중복 체크", description = "아이디의 중복 여부를 확인하는 API. 중복일 경우 result 값 true")
    public ApiResponse<UserResponseDTO.CheckAccountIdResultDTO> checkAccountIdDuplication(
            @RequestParam("accountId") String accountId) {
        UserResponseDTO.CheckAccountIdResultDTO response = userCommandService.checkAccountIdDuplication(accountId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "사용자 검색", description = "검색어를 포함하는 모든 사용자의 accountId를 리스트로 반환")
    @GetMapping("/search")
    public ApiResponse<List<UserResponseDTO.UserSearchDTO>> searchUsers(@RequestParam String keyword) {
        List<UserResponseDTO.UserSearchDTO> users = userCommandService.searchUsersByAccountId(keyword);
        return ApiResponse.onSuccess(users);
    }
}
