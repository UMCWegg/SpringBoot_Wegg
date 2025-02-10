package umc.wegg.service.MapService;

import umc.wegg.dto.MapResponseDTO;

public interface MapService {
//    MapResponseDTO.ReverseGeocodingDTO reverseGeocoding(String latitude, String longitude);
    MapResponseDTO.SearchDTO searchPlacesByKeyword(String keyword, String latitude, String longitude, Integer radius);
}
