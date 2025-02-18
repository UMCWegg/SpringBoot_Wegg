package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.service.MapService.MapService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapRestController {

    private final MapService mapService;

    @GetMapping("/plans/search")
    @Operation(summary = "장소 검색(계획)", description = "계획 설정 시, 장소를 지정할때 사용하는 장소 검색 API (사용자 위치 주변 장소 리스트 반환)")
    public ApiResponse<MapResponseDTO.SearchPlanPlaceListDTO> searchPlan(@Valid @ModelAttribute MapRequestDTO.SearchPlanDTO request,
                                                                         @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                         @RequestParam(name = "size", defaultValue = "15") Integer size) {

        MapResponseDTO.SearchPlanPlaceListDTO response = mapService.searchPlaceListByKeyword(request, page, size);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/hotplaces/search")
    @Operation(summary = "장소 검색(핫플)", description = "주변 weggy 핫플 장소 검색 API (사용자 위치 주변 장소 리스트 반환)")
    public ApiResponse<MapResponseDTO.SearchHotPlaceListDTO> searchHotPlace(@Valid @ModelAttribute MapRequestDTO.SearchHotPlaceDTO request,
                                                                            @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                            @RequestParam(name = "size", defaultValue = "15") Integer size) {

        MapResponseDTO.SearchHotPlaceListDTO response = mapService.searchHotPlaceListByKeyword(request, page, size);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/hotplaces")
    @Operation(summary = "주변 weggy 핫플 조회", description = "사용자의 화면을 기준으로 weggy 핫플을 조회하는 API")
    public ApiResponse<MapResponseDTO.HotPlaceListDTO> getAddressesInView(
            @Valid @ModelAttribute MapRequestDTO.ViewHotPlaceDTO request,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "15") Integer size
    ) {

        MapResponseDTO.HotPlaceListDTO response = mapService.viewHotPlaceList(request, page, size);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/addresses/{address_id}/bookmark")
    @Operation(summary = "장소 저장", description = "장소를 저장하는 API")
    public ApiResponse<MapResponseDTO.BookmarkDTO> bookmarkAddress(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("address_id") Long addressId
    ) {

        MapResponseDTO.BookmarkDTO response = mapService.bookmarkAddress(authenticatedUser, addressId);

        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/addresses/{address_id}/bookmark")
    @Operation(summary = "장소 저장 삭제", description = "저장한 장소를 삭제하는 API")
    public ApiResponse<MapResponseDTO.UnbookmarkDTO> unbookmarkAddress(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("address_id") Long addressId
    ) {

        MapResponseDTO.UnbookmarkDTO response = mapService.unbookmarkAddress(authenticatedUser, addressId);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/details")
    @Operation(summary = "장소 상세 조회", description = "placeName을 기준으로 장소 상세 정보를 조회하는 API")
    public ApiResponse<MapResponseDTO.DetailListDTO> getPlaceDetails(
            @Valid @ModelAttribute MapRequestDTO.SearchDetailDTO request) {

        MapResponseDTO.DetailListDTO response = mapService.getPlaceDetails(request);
        return ApiResponse.onSuccess(response);
    }

}
