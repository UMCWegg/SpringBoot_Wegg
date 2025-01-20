package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;

import java.util.List;

public interface TodoRepository extends JpaRepository<TodoList, Long> {
    // 특정 사용자(userId)의 Todo 리스트를 상태별로 조회
    List<TodoList> findByUserId(Long userId);

    // 상태가 DONE인 TodoList의 개수를 반환
    long countByUserIdAndStatus(Long userId, TodoListStatus status);

    // 전체 TodoList 개수를 반환
    long count();
}