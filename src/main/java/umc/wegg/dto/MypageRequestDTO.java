package umc.wegg.dto;

import lombok.Getter;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.AlarmType;

public class MypageRequestDTO {
//    @Getter
//    public static class EditDTO {
//        private String accountId;
//        private String name;
//        private String profileImage;
//    }

    @Getter
    public static class SettingDTO {
        private AlarmType postAlarm;
        private AlarmType commentAlarm;
        private AlarmType placeAlarm;
        private AlarmType randomAlarm;
        private AlarmType eggAlarm;
        private boolean marketingAgree;
        private boolean placeCheck;
        private boolean randomCheck;
        private AccountVisibility accountVisibility;
    }
}
