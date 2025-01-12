package umc.wegg.dto;

import lombok.Getter;
import umc.wegg.domain.enums.ReadStatus;

public class NotificationRequestDTO {


    @Getter
    public static class ReadDTO{
        ReadStatus readStatus;
    }
}
