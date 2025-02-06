package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.service.MapService.MapService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapRestController {

    private final MapService mapService;

    @PostMapping("/search")
    @Operation(summary = "장소 검색(계획)",description = "계획 설정 시, 장소를 지정할때 사용하는 장소 검색 API (사용자 위치 주변 장소 리스트 반환)")
    public ApiResponse<MapResponseDTO.PlaceListDTO> search(@RequestBody @Valid MapRequestDTO.SearchDTO request) {

        MapResponseDTO.PlaceListDTO response = mapService.searchPlaceListByKeyword(request);

        return ApiResponse.onSuccess(response);
    }
}
