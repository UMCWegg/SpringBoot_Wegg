package umc.wegg.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.service.TodoService.TodoCommandService;
import umc.wegg.web.dto.TodoRequestDTO;
import umc.wegg.web.dto.TodoResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoRestController {

    private final TodoCommandService todoCommandService;

    @PostMapping("/add")
    public ApiResponse<TodoResponseDTO.AddResultDTO> join(@RequestBody @Valid TodoRequestDTO.AddDTO request){
        TodoList todo = todoCommandService.addTodo(request);
        return ApiResponse.onSuccess(TodoConverter.toAddResultDTO(todo));
    }

    @PatchMapping("/{todo_Id}")
    public ApiResponse<TodoResponseDTO.AddResultDTO> updateTodo(
            @PathVariable Long todoId,
            @RequestBody @Valid TodoRequestDTO.UpdateDTO request) {
        TodoList updatedTodo = todoCommandService.updateTodo(todoId, request);
        return ApiResponse.onSuccess(TodoConverter.toAddResultDTO(updatedTodo));
    }

    @GetMapping("/achievement")
    public ApiResponse<Double> getAchievementRate() {
        double achievementRate = todoCommandService.getAchievementRate();  // 비율 계산
        return ApiResponse.onSuccess(achievementRate);  // 비율 반환
    }


}
