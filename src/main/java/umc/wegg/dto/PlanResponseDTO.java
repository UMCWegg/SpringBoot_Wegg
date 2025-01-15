package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.enums.ReplayStatus;

import java.time.LocalDateTime;

public class PlanResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanAddResultDTO{
        Long planId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDetailDTO {
        Long planId;
        PlanStatus status;
        ReplayStatus replay;
        LocalDateTime startTime;
        LocalDateTime finishTime;
        Integer lateTime;
        Float latitude;
        Float longitude;
        String address;
        String content;
        Long userId;
    }

}
