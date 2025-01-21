package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;

public interface TodoRepository extends JpaRepository<TodoList, Long> {
    // 상태가 DONE인 TodoList의 개수를 반환
    long countByStatus(TodoListStatus status);

    // 전체 TodoList 개수를 반환
    long count();
}