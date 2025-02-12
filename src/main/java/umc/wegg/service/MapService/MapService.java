package umc.wegg.service.MapService;

import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;

public interface MapService {
    MapResponseDTO.SearchPlanPlaceListDTO searchPlaceListByKeyword(MapRequestDTO.SearchPlanDTO request);
    MapResponseDTO.SearchHotPlaceListDTO searchHotPlaceListByKeyword(MapRequestDTO.SearchHotPlaceDTO request);
    MapResponseDTO.HotPlaceListDTO viewHotPlaceList(double minX, double maxX, double minY, double maxY, String sortBy);
    MapResponseDTO.BookmarkDTO bookmarkAddress(AuthenticatedUser authenticatedUser, Long addressId);
}
