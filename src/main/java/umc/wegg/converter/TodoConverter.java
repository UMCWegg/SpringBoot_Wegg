package umc.wegg.converter;

import umc.wegg.domain.TodoList;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.repository.UserRepository;
import umc.wegg.dto.TodoRequestDTO;
import umc.wegg.dto.TodoResponseDTO;

import java.time.LocalDateTime;

public class TodoConverter {
    public static TodoResponseDTO.AddResultDTO toAddResultDTO(TodoList todo) {
        return TodoResponseDTO.AddResultDTO.builder()
                .todoId(todo.getId())
                .createdAt(todo.getCreatedAt()) // TodoList의 createdAt 사용
                .build();
    }

    public static TodoList toTodo(TodoRequestDTO.AddDTO request, UserRepository userRepository) {
        TodoListStatus status = request.getStatus() != null ? request.getStatus() : TodoListStatus.YET;

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 현재 시간을 기준으로 date 필드 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.getHour() < 4
                ? now.minusDays(1).withHour(4).withMinute(0).withSecond(0).withNano(0)
                : now.withHour(4).withMinute(0).withSecond(0).withNano(0);

        return TodoList.builder()
                .status(status)
                .content(request.getContent())
                .user(user)
                .date(startOfDay) // date 필드 설정
                .build();
    }
}
