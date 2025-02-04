package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.wegg.domain.enums.TodoListStatus;

import java.time.LocalDateTime;

public class TodoResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddResultDTO{
        Long todoId;
        String content;
        TodoListStatus status;
        LocalDateTime createdAt;
    }
}
