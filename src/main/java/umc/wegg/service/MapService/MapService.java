package umc.wegg.service.MapService;

import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;

public interface MapService {
    MapResponseDTO.SearchPlanPlaceListDTO searchPlaceListByKeyword(MapRequestDTO.SearchPlanDTO request, Integer page, Integer size);
    MapResponseDTO.SearchHotPlaceListDTO searchHotPlaceListByKeyword(MapRequestDTO.SearchHotPlaceDTO request, Integer page, Integer size);
    MapResponseDTO.HotPlaceListDTO viewHotPlaceList(MapRequestDTO.ViewHotPlaceDTO request, Integer page, Integer size);
    MapResponseDTO.BookmarkDTO bookmarkAddress(AuthenticatedUser authenticatedUser, Long addressId);
}
