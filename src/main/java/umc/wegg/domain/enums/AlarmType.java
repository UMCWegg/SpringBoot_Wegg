package umc.wegg.domain.enums;

public enum AlarmType {
    MUTE_SINGLE,       // 무음 + 단발
    MUTE_REPEAT,       // 무음 + 연속
    VIBRATE_SINGLE,    // 진동 + 단발
    VIBRATE_REPEAT,    // 진동 + 연속
    SOUND_SINGLE,      // 소리 + 단발
    SOUND_REPEAT,      // 소리 + 연속
    BOTH_SINGLE,       // 진동 + 소리 + 단발
    BOTH_REPEAT        // 진동 + 소리 + 연속
}