package umc.wegg.service.TodoService;

import umc.wegg.domain.TodoList;
import umc.wegg.dto.TodoRequestDTO;
import umc.wegg.dto.TodoResponseDTO;

import java.util.List;

public interface TodoCommandService {
    TodoList addTodo(TodoRequestDTO.AddDTO request);
    TodoList updateTodo(Long todoId, TodoRequestDTO.UpdateDTO request); // updateTodo 추가
    TodoList checkTodo(Long todoId, TodoRequestDTO.CheckDTO request);
    double getAchievementRate(Long userId);
    List<TodoList> getUserTodos(Long userId);
    TodoResponseDTO.DeleteResultDTO deleteTodo(Long todoId);
}
