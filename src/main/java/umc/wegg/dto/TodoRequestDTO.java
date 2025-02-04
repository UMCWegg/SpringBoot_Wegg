package umc.wegg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import umc.wegg.domain.enums.TodoListStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class TodoRequestDTO {
    @Getter
    @Setter
    public static class AddDTO {
        @JsonIgnore
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
    }
}
