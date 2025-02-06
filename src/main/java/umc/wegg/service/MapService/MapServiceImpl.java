package umc.wegg.service.MapService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.util.MapUtil;
import umc.wegg.util.RedisUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final MapUtil mapUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper; // JSON 직렬화/역직렬화에 사용

    @Override
    public MapResponseDTO.PlaceListDTO searchPlaceListByKeyword(MapRequestDTO.SearchDTO request) {
        MapResponseDTO.SearchDTO searchPlaces = mapUtil.searchPlacesByKeyword(request.getKeyword(), request.getLatitude(), request.getLongitude(), 2000);

        // PlaceListDTO 생성 및 검색된 장소 이름 추가
        List<MapResponseDTO.PlaceListDTO.PlaceNameDTO> placeList = searchPlaces.getSearchByKeywordList().stream()
                .map(place -> new MapResponseDTO.PlaceListDTO.PlaceNameDTO(place.getPlaceName())) // 장소 이름 DTO로 변환
                .collect(Collectors.toList());

        // Redis에 검색 결과 저장
        for (MapResponseDTO.SearchDTO.SearchByKeywordDTO place : searchPlaces.getSearchByKeywordList()) {
            try {
                String key = place.getPlaceName();
                String value = objectMapper.writeValueAsString(place); // place detail
                redisUtil.setDataExpire(key, value, 10 * 60); // Redis에 저장(key-place name/value-place detail)
            } catch (Exception e) {
                e.printStackTrace(); // 직렬화 실패 시 예외 처리
            }
        }

        // PlaceListDTO 생성
        return MapResponseDTO.PlaceListDTO.builder()
                .placeList(placeList)
                .build();

    }
}
