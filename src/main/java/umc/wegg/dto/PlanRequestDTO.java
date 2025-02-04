package umc.wegg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import umc.wegg.domain.enums.LateStatus;
import umc.wegg.domain.enums.PlanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PlanRequestDTO {

    @Getter
    @Setter
    public static class PlanAddDTO{
        @JsonIgnore
        Long userId;

        @NotNull
        PlanStatus status;

        @NotNull
        List<LocalDate> planDates;

        @NotNull
        @JsonFormat(pattern = "HH:mm")  // 시와 분만 받도록 포맷을 지정
        LocalTime startTime;

        @NotNull
        @JsonFormat(pattern = "HH:mm")  // 시와 분만 받도록 포맷을 지정
        LocalTime finishTime;

        @NotNull
        LateStatus lateTime;

        @NotNull
        Long addressId; // Address 엔티티와 연결하기 위한 addressId 추가


        @NotNull
        Boolean planOn;

        public LocalTime getStartTime() {
            return startTime.truncatedTo(ChronoUnit.MINUTES);  // 초와 나노초를 잘라냄
        }

        public LocalTime getFinishTime() {
            return finishTime.truncatedTo(ChronoUnit.MINUTES);  // 초와 나노초를 잘라냄
        }
    }


    @Getter
    @Setter
    public static class PlanUpdateDTO {
        @JsonFormat(pattern = "HH:mm")  // 시와 분만 받도록 포맷을 지정
        LocalTime startTime;
        @JsonFormat(pattern = "HH:mm")  // 시와 분만 받도록 포맷을 지정
        LocalTime finishTime;
        LateStatus lateTime;
        Long addressId;
        public LocalTime getStartTime() {
            return startTime.truncatedTo(ChronoUnit.MINUTES);  // 초와 나노초를 잘라냄
        }

        public LocalTime getFinishTime() {
            return finishTime.truncatedTo(ChronoUnit.MINUTES);  // 초와 나노초를 잘라냄
        }
    }

    @Getter
    public static class PlanOnoffDTO {
        Boolean planOn;
    }
    @Getter
    public static class PlanStatusDTO {
        PlanStatus planStatus;
    }
}
