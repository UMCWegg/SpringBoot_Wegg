package umc.wegg.dto;

import lombok.Getter;
import umc.wegg.domain.enums.TodoListStatus;

public class TodoRequestDTO {
    @Getter
    public static class AddDTO {
        private Long userId;
        private TodoListStatus status;
        private String content;
    }

    @Getter
    public static class UpdateDTO {
        private TodoListStatus status;
        private String content;
        private Long userId;
    }
}
