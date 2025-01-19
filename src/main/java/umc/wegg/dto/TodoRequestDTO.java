package umc.wegg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import umc.wegg.domain.enums.TodoListStatus;

public class TodoRequestDTO {
    @Getter
    @Setter
    public static class AddDTO {
        @NotNull
        private Long userId;
        @NotNull
        private TodoListStatus status;
        @NotNull
        private String content;
    }

    @Getter
    public static class UpdateDTO {
        private TodoListStatus status;
        private String content;
        private Long userId;
    }
}
