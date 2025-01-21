package umc.wegg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.enums.ReplayStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlanRequestDTO {
    @Getter
    @Setter
    public static class PlanAddDTO{
        @JsonIgnore
        Long userId;
        @NotNull
        PlanStatus status;
        @NotNull
        ReplayStatus replay;
        @NotNull
        LocalDateTime startTime;
        @NotNull
        LocalDateTime finishTime;
        @NotNull
        Integer lateTime;
        @NotNull
        Float latitude;
        @NotNull
        Float longitude;
        @NotNull
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
        String address;
    }
}
