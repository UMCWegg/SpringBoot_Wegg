package umc.wegg.dto;

import lombok.Getter;
import umc.wegg.domain.enums.ReadStatus;
import umc.wegg.domain.enums.TodoListStatus;

public class NotificationRequestDTO {
    @Getter
    public static class ReadDTO {
        private ReadStatus readStatus;
    }
}
