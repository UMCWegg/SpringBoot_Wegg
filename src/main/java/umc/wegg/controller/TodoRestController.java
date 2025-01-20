package umc.wegg.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import umc.wegg.converter.TodoConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.service.TodoService.TodoCommandService;
import umc.wegg.dto.TodoRequestDTO;
import umc.wegg.dto.TodoResponseDTO;
import org.springframework.security.core.Authentication;


@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoRestController {

    private final TodoCommandService todoCommandService;

    @PostMapping("/add")
    public ApiResponse<TodoResponseDTO.AddResultDTO> join(@RequestBody @Valid TodoRequestDTO.AddDTO request){
        // 인증된 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getId(); // 인증된 사용자 ID
        Long userId = 2L;
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

    @GetMapping("/achievement")
    public ApiResponse<Double> getAchievementRate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 2L;  // 실제 인증된 사용자 ID로 교체

        double achievementRate = todoCommandService.getAchievementRate(userId);  // 비율 계산
        return ApiResponse.onSuccess(achievementRate);  // 비율 반환
    }


}
