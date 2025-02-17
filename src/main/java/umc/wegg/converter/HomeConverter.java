package umc.wegg.converter;

import org.springframework.stereotype.Component;
import umc.wegg.domain.Egg;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Post;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.EggStatus;
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
    public List<HomeResponseDTO.PlanInfo> convertPlansToPlanInfos(List<Plan> plans, List<Egg> eggs) {
        return plans.stream()
                .map(plan -> {
                    EggStatus eggStatus = eggs.stream()
                            .filter(egg -> egg.getPlan().getId().equals(plan.getId()))
                            .map(Egg::getStatus)
                            .findFirst()
                            .orElse(EggStatus.INTACT); // 기본적으로 INTACT 처리

                    return new HomeResponseDTO.PlanInfo(
                            plan.getId(),
                            plan.getStartTime(),
                            plan.getFinishTime(),
                            plan.getStatus(),
                            eggStatus
                    );
                })
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




}

