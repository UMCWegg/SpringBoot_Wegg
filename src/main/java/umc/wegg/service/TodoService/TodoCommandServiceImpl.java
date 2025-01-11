package umc.wegg.service.TodoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.TodoList;
import umc.wegg.repository.TodoRepository;
import umc.wegg.web.dto.TodoRequestDTO;

@Service
@RequiredArgsConstructor
public class TodoCommandServiceImpl implements TodoCommandService{

    private final TodoRepository todoRepository;

    @Override
    public TodoList listTodo(TodoRequestDTO.ListDTO request) {

        return null;
    }
}