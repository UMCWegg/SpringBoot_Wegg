package umc.wegg.service.TodoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.repository.TodoRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.dto.TodoRequestDTO;

@Service
@RequiredArgsConstructor
public class TodoCommandServiceImpl implements TodoCommandService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;  // UserRepository 추가

    @Override
    public TodoList addTodo(TodoRequestDTO.AddDTO request) {
        TodoList newTodo = TodoConverter.toTodo(request, userRepository);
        return todoRepository.save(newTodo);
    }

    @Override
    public TodoList updateTodo(Long todoId, TodoRequestDTO.UpdateDTO request) {
        TodoList existingTodo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (request.getStatus() != null) {
            existingTodo.setStatus(request.getStatus());
        }
        if (request.getContent() != null) {
            existingTodo.setContent(request.getContent());
        }

        return todoRepository.save(existingTodo);
    }

    public double getAchievementRate(Long userId) {
        long totalTodos = todoRepository.count();  // 전체 Todo 개수
        long doneTodos = todoRepository.countByUserIdAndStatus(userId, TodoListStatus.DONE);  // DONE 상태의 Todo 개수

        if (totalTodos == 0) {
            return 0.0;  // 전체 할 일이 없다면 0%로 처리
        }

        return (doneTodos * 100.0) / totalTodos;  // 비율 계산
    }
}
