package umc.wegg.service.TodoService;

import umc.wegg.domain.TodoList;
import umc.wegg.dto.TodoRequestDTO;

public interface TodoCommandService {
    TodoList addTodo(TodoRequestDTO.AddDTO request);
    TodoList updateTodo(Long todoId, TodoRequestDTO.UpdateDTO request); // updateTodo 추가
    double getAchievementRate(Long userId);
}
