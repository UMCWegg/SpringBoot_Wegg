package umc.wegg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        LocalDate planDate;
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime;
        @JsonFormat(pattern = "HH:mm")
        LocalTime finishTime;
        LateStatus lateTime;
        Boolean Onoff;
        Float latitude;
        Float longitude;
        String placeName;
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

    public static class LocationVerificationResponseDTO {
        private String message;
        private Boolean success;

        public LocationVerificationResponseDTO(String message, Boolean success) {
            this.message = message;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public Boolean getSuccess() {
            return success;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPlanInfoDTO{
        Long planId;
        String placeName;
        Double latitude;
        Double longitude;
        LocalDateTime startTime;
    }

}
