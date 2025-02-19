package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.service.TodoService.TodoCommandService;
import umc.wegg.dto.TodoRequestDTO;
import umc.wegg.dto.TodoResponseDTO;
import org.springframework.security.core.Authentication;
import umc.wegg.validation.annotation.ValidUser;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoRestController {

    private final TodoCommandService todoCommandService;

    @PostMapping("/add")
    public ApiResponse<TodoResponseDTO.AddResultDTO> join(
            @ValidUser @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody @Valid TodoRequestDTO.AddDTO request) {

        // 인증된 사용자 ID 가져오기
        Long userId = authenticatedUser.getUserId();

        // AddDTO에 userId 설정
        request.setUserId(userId);

        TodoList todo = todoCommandService.addTodo(request);
        return ApiResponse.onSuccess(TodoConverter.toAddResultDTO(todo));
    }

    @PatchMapping("/{todo_id}")
    public ApiResponse<TodoResponseDTO.AddResultDTO> updateTodo(
            @PathVariable("todo_id") Long todoId,
            @RequestBody @Valid TodoRequestDTO.UpdateDTO request) {
        TodoList updatedTodo = todoCommandService.updateTodo(todoId, request);
        return ApiResponse.onSuccess(TodoConverter.toAddResultDTO(updatedTodo));
    }
    @PatchMapping("/{todo_id}/check")
    public ApiResponse<TodoResponseDTO.AddResultDTO> checkTodo(
            @PathVariable("todo_id") Long todoId,
            @RequestBody @Valid TodoRequestDTO.CheckDTO request) {
        TodoList checkTodo = todoCommandService.checkTodo(todoId, request);
        return ApiResponse.onSuccess(TodoConverter.toAddResultDTO(checkTodo));
    }

    @GetMapping("/achievement")
    public ApiResponse<Double> getAchievementRate(@ValidUser @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = authenticatedUser.getUserId();

        double achievementRate = todoCommandService.getAchievementRate(userId);  // 비율 계산
        return ApiResponse.onSuccess(achievementRate);  // 비율 반환
    }

    @GetMapping
    public ApiResponse<List<TodoResponseDTO.AddResultDTO>> getUserTodos(
            @ValidUser @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId();
        List<TodoList> todos = todoCommandService.getUserTodos(userId);

        List<TodoResponseDTO.AddResultDTO> result = todos.stream()
                .map(TodoConverter::toAddResultDTO)
                .toList();

        return ApiResponse.onSuccess(result);
    }


    @DeleteMapping("/{todo_id}")
    public ApiResponse<TodoResponseDTO.DeleteResultDTO> deleteTodo(@PathVariable Long todo_id) {
        TodoResponseDTO.DeleteResultDTO response = todoCommandService.deleteTodo(todo_id);
        return ApiResponse.onSuccess(response);
    }
//    @DeleteMapping("/{plan_id}")
//    public ApiResponse<PlanResponseDTO.PlanDeleteResponseDTO> deletePlan(@PathVariable Long plan_id) {
//        PlanResponseDTO.PlanDeleteResponseDTO response = planCommandService.deletePlan(plan_id);
//        return ApiResponse.onSuccess(response); // 삭제된 계획 정보를 반환
//    }

}
