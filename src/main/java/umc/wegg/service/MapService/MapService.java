package umc.wegg.service.MapService;

import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;

public interface MapService {
    MapResponseDTO.PlaceListDTO searchPlaceListByKeyword(MapRequestDTO.SearchDTO request);
}
