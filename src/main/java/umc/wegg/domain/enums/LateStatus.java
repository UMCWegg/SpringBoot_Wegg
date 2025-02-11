package umc.wegg.domain.enums;

public enum LateStatus {
    ZERO(0),
    THREE(3),
    FIVE(5),
    SEVEN(7),
    TEN(10);

    private final int minutes;

    LateStatus(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
}