package umc.wegg.service.TodoService;

import umc.wegg.domain.TodoList;
import umc.wegg.web.dto.TodoRequestDTO;

public interface TodoCommandService {
    TodoList listTodo(TodoRequestDTO.ListDTO request);
}
