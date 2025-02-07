package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.service.MapService.MapService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapRestController {

    private final MapService mapService;

    @PostMapping("/plans/search")
    @Operation(summary = "장소 검색(계획)", description = "계획 설정 시, 장소를 지정할때 사용하는 장소 검색 API (사용자 위치 주변 장소 리스트 반환)")
    public ApiResponse<MapResponseDTO.SearchPlanPlaceListDTO> searchPlan(@RequestBody @Valid MapRequestDTO.SearchPlanDTO request) {

        MapResponseDTO.SearchPlanPlaceListDTO response = mapService.searchPlaceListByKeyword(request);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/hotplaces/search")
    @Operation(summary = "장소 검색(핫플)", description = "주변 weggy 핫플 장소 검색 API (사용자 위치 주변 장소 리스트 반환)")
    public ApiResponse<MapResponseDTO.SearchHotPlaceListDTO> searchHotPlace(@RequestBody @Valid MapRequestDTO.SearchHotPlaceDTO request) {

        MapResponseDTO.SearchHotPlaceListDTO response = mapService.searchHotPlaceListByKeyword(request);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/hotplaces")
    @Operation(summary = "주변 weggy 핫플 조회", description = "사용자의 화면을 기준으로 weggy 핫플을 조회하는 API")
    public ApiResponse<MapResponseDTO.HotPlaceListDTO> getAddressesInView(
            @RequestParam(name = "minX") double minX,   //최소 경도
            @RequestParam(name = "maxX") double maxX,   //최대 경도
            @RequestParam(name = "minY") double minY,   //최소 위도
            @RequestParam(name = "maxY") double maxY,   //최대 위도
            @RequestParam(name = "sortBy") String sortBy //정렬 기준
    ) {

        MapResponseDTO.HotPlaceListDTO response = mapService.viewHotPlaceList(minX, maxX, minY, maxY, sortBy);

        return ApiResponse.onSuccess(response);
    }

}
