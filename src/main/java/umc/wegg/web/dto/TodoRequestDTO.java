package umc.wegg.web.dto;

import lombok.Getter;
import umc.wegg.domain.enums.TodoListStatus;

public class TodoRequestDTO {
    @Getter
    public static class ListDTO{
        TodoListStatus status;
        String content;
    }
}
