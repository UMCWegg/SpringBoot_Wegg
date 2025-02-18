package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.wegg.domain.enums.LateStatus;
import umc.wegg.domain.enums.PlanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PlanResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanAddResultDTO {
        Long planId;
        LocalDateTime createdAt;
        String warningMessage;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanUpdateResultDTO{
        Long planId;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDetailDTO {
        Long planId;
        PlanStatus status;
        LocalDateTime startTime;
        LocalDateTime finishTime;
        LateStatus lateTime;
        Float latitude;
        Float longitude;
        String address;
        String content;
        Long userId;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanStatusDTO{
        Long planId;
        PlanStatus planStatus;
    }

    // 삭제된 계획의 응답 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDeleteResponseDTO {
        Long planId;
        LocalDate planDate;
        LocalTime startTime;
        String address;
    }
    // 장소 인증 응답 DTO
    public static class LocationVerificationResponseDTO {
        private String message;

        public LocationVerificationResponseDTO(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
