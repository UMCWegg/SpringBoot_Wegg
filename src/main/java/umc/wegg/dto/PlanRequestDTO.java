package umc.wegg.dto;

import lombok.Getter;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.enums.ReplayStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlanRequestDTO {
    @Getter
    public static class PlanAddDTO{
        PlanStatus status;
        ReplayStatus replay;
        LocalDateTime startTime;
        LocalDateTime finishTime;
        Long userId;
        Integer lateTime;
        Float latitude;
        Float longitude;
        String address;

    }

    @Getter
    public static class PlanUpdateDTO {
        PlanStatus status;
        ReplayStatus replay;
        LocalDateTime startTime;
        LocalDateTime finishTime;
        Long userId;
        Integer lateTime;
        Float latitude;
        Float longitude;
        String content;
    }
}
