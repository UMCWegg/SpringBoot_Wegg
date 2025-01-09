package umc.wegg.domain.enums;

public enum ReplayStatus {
    // 아이폰 알람에서의 반복 기능 따라함
    DAILY("매일"),
    SUNDAY("일요일마다"),
    MONDAY("월요일마다"),
    TUESDAY("화요일마다"),
    WEDNESDAY("수요일마다"),
    THURSDAY("목요일마다"),
    FRIDAY("금요일마다"),
    SATURDAY("토요일마다");

    private final String description;

    ReplayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
