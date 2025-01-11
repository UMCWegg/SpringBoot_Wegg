package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.TodoList;

public interface TodoRepository extends JpaRepository<TodoList, Long> {
}