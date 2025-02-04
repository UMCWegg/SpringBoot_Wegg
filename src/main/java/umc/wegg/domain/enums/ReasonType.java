package umc.wegg.domain.enums;

public enum ReasonType {

    HABIT_FORMATION("공부 습관을 형성하기 위해서"),
    FOLLOW_FRIENDS("친구들을 따라서"),
    STUDY_RECORD("공부를 기록하기 위해서"),
    FIND_GOOD_PLACE("주변의 공부하기 좋은 장소를 찾기 위해서"),
    CUSTOM_INPUT("직접 입력하기");

    private final String description;

    ReasonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
