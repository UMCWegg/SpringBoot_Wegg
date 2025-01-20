package umc.wegg.service.TodoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.repository.TodoRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.dto.TodoRequestDTO;

import java.util.List;

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

    @Override
    public double getAchievementRate(Long userId) {
        List<TodoList> userTodos = todoRepository.findByUserId(userId);  // userId에 해당하는 Todo 리스트
        long totalTodos = userTodos.size();  // 전체 할 일 개수
        long doneTodos = todoRepository.countByUserIdAndStatus(userId, TodoListStatus.DONE);  // DONE 상태의 Todo 개수

        if (totalTodos == 0) {
            return 0.0;  // 해당 사용자가 할 일이 없으면 0%로 처리
        }

        return (doneTodos * 100.0) / totalTodos;  // 비율 계산
    }
}
