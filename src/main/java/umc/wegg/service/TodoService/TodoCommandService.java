package umc.wegg.service.TodoService;

import umc.wegg.domain.TodoList;
import umc.wegg.dto.TodoRequestDTO;

import java.util.List;

public interface TodoCommandService {
    TodoList addTodo(TodoRequestDTO.AddDTO request);
    TodoList updateTodo(Long todoId, TodoRequestDTO.UpdateDTO request); // updateTodo 추가
    double getAchievementRate(Long userId);
    List<TodoList> getUserTodos(Long userId);
    void deleteTodo(Long todoId);
}
