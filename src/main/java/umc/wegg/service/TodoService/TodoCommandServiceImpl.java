package umc.wegg.service.TodoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.repository.TodoRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.web.dto.TodoRequestDTO;

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
}
