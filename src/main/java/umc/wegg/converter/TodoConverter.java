package umc.wegg.converter;

import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.web.dto.TodoRequestDTO;
import umc.wegg.web.dto.TodoResponseDTO;

import java.time.LocalDateTime;

public class TodoConverter {
    public static TodoResponseDTO.AddResultDTO toAddResultDTO(TodoList todo){
        return TodoResponseDTO.AddResultDTO.builder()
                .todoId(todo.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static TodoList toTodo(TodoRequestDTO.AddDTO request) {
        // status가 null이면 기본값으로 YET
        TodoListStatus status = request.getStatus() != null ? request.getStatus() : TodoListStatus.YET;


        return TodoList.builder()
                .status(status)
                .content(request.getContent())
                .build();
    }


}
