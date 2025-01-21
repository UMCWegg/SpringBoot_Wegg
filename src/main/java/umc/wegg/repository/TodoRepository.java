package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<TodoList, Long> {
    // 상태가 DONE인 TodoList의 개수를 반환
    long countByStatus(TodoListStatus status);

    // 전체 TodoList 개수를 반환
    long count();

    // 특정 날짜의 투두리스트 검색
//    @Query("SELECT t FROM TodoList t WHERE DATE(t.createdAt) = :date")
//    List<TodoList> findTodosByDate(LocalDate date);

    @Query("SELECT t FROM TodoList t WHERE t.user.id = :userId AND DATE(t.date) = :date")
    List<TodoList> findTodosByUserIdAndDate(@Param("userId") Long userId,
                                            @Param("date")LocalDate date);

}