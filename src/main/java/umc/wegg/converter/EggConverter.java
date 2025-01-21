package umc.wegg.converter;

import umc.wegg.domain.Time;
import umc.wegg.domain.User;
import umc.wegg.dto.TimeRequestDTO;

import java.time.LocalDateTime;

public class EggConverter {

    // TimeRequestDTO -> Time 엔티티 변환
    public static Time toTime(TimeRequestDTO request, User user) {
        // 서버에서 기록 날짜를 자동 계산 (다음날 새벽 4시까지는 이전 날짜로 처리)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recordDate = now.getHour() < 4 ? now.minusDays(1).toLocalDate().atStartOfDay() : now.toLocalDate().atStartOfDay();

        return Time.builder()
                .duration(request.getDuration())
                .date(recordDate)
                .user(user)
                .build();
    }
}
