package umc.wegg.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.service.TodoService.TodoCommandService;
import umc.wegg.web.dto.TodoRequestDTO;
import umc.wegg.web.dto.TodoResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoRestController {

    private final TodoCommandService todoCommandService;

    @PostMapping("/")
    public ApiResponse<TodoResponseDTO.ListResultDTO> join(@RequestBody @Valid TodoRequestDTO.ListDTO request){
        return null;
    }
}