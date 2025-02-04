package umc.wegg.converter;

import org.springframework.stereotype.Component;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Post;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.repository.TimeRepository;
import umc.wegg.repository.TodoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HomeConverter {

    // Plan -> PlanInfo 변환
    public List<HomeResponseDTO.PlanInfo> convertPlansToPlanInfos(List<Plan> plans) {
        return plans.stream()
                .map(plan -> new HomeResponseDTO.PlanInfo(
                        plan.getId(),
                        plan.getStartTime(),
                        plan.getFinishTime()
                ))
                .collect(Collectors.toList());
    }

    // Post -> PostInfo 변환
    public List<HomeResponseDTO.PostInfo> convertPostsToPostInfos(List<Post> posts) {
        return posts.stream()
                .map(post -> new HomeResponseDTO.PostInfo(
                        post.getId(),
                        post.getImageUrl(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // TodoList -> TodoInfo 변환
    public List<HomeResponseDTO.TodoInfo> convertTodosToTodoInfos(List<TodoList> todos) {
        return todos.stream()
                .map(todo -> new HomeResponseDTO.TodoInfo(
                        todo.getId(),
                        todo.getContent(),
                        todo.getStatus(),
                        todo.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    // 날짜별 공부 시간 및 투두리스트 달성률 계산
    public List<HomeResponseDTO.DateSummaryInfo> calculateDateSummaries(
            Long userId,
            LocalDate start,
            LocalDate end,
            TimeRepository timeRepository,
            TodoRepository todoRepository
    ) {
        return start.datesUntil(end.plusDays(1)) // 시작일부터 종료일까지 반복
                .map(date -> {
                    // 공부 시간 계산
                    int studyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, date)
                            .stream()
                            .mapToInt(time -> time.getDuration())
                            .sum();

                    // 투두리스트 정보 계산
                    List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, date);
                    int totalTodos = todos.size();
                    int completedTodos = (int) todos.stream().filter(todo -> todo.getStatus() == TodoListStatus.DONE).count();
                    double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

                    // 통합 정보 생성
                    return new HomeResponseDTO.DateSummaryInfo(date, studyTime, totalTodos, completedTodos, completionRate);
                })
                .collect(Collectors.toList());
    }

}

