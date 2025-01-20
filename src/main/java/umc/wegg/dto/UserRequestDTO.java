package umc.wegg.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import umc.wegg.domain.enums.Job;
import umc.wegg.domain.enums.Role;

import java.util.List;

public class UserRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJoinDto{

        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        @NotNull(message = "이메일은 필수 항목입니다.")
        private String email; // 사용자 이메일

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        private String password; // 비밀번호

        @NotNull
        private Boolean marketingAgree;

        @NotBlank(message = "계정 ID는 필수 항목입니다.")
        @Size(max = 10, message = "계정 ID는 최대 10자까지 가능합니다.")
        private String accountId; // 사용자 계정 ID

        @NotBlank(message = "이름은 필수 항목입니다.")
        @Size(max = 10, message = "이름은 최대 10자까지 가능합니다.")
        private String name; // 사용자 이름

        private Job job; // 사용자 신분

        private String reason; // 이 앱을 시작한 이유

        @Pattern(regexp = "\\d{3}\\d{4}\\d{4}", message = "전화번호 형식이 맞아야 합니다.")
        @NotBlank(message = "전화번호는 필수 항목입니다.")
        private String phone; // 사용자 전화번호


        @NotNull
        private Boolean alarm;

        // 연락처 리스트
        @Size(max = 10, message = "최대 10개의 연락처를 제공할 수 있습니다.")
        private List<ContactDto> contact; // 연락처 정보 리스트
    }

    @Getter
    @Builder
    //역직렬화 시 필요
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactDto {
        private String contactName;
        @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}", message = "전화번호 형식이 맞아야 합니다.")
        private String phone; // 연락처 전화번호
    }

    @Getter
    @Setter
    public static class SendPhoneVerificationDto {
        @Pattern(regexp = "\\d{3}\\d{4}\\d{4}", message = "전화번호 형식이 맞아야 합니다.")
        private String phone; // 연락처 전화번호
    }
}
