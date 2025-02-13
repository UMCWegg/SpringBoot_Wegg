package umc.wegg.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import umc.wegg.dto.MapResponseDTO;

@Component
@RequiredArgsConstructor
public class MapUtil {

    @Value("${spring.kakao.api.client-id}")
    private String CLIENT_ID;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final double EARTH_RADIUS = 6371; // 지구 반지름 (킬로미터)

    //키워드로 장소 검색하기 API(kakao)
    public MapResponseDTO.SearchDTO searchPlacesByKeyword(String keyword, String latitude, String longitude, Integer radius, Integer page, Integer size) {
        StringBuilder url = new StringBuilder("https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword);

        // latitude, longitude, radius가 null이 아닐 때만 추가
        if (longitude != null && latitude != null) {
            url.append("&x=").append(longitude).append("&y=").append(latitude);
        }
        if (radius != null) {
            url.append("&radius=").append(radius);
        }
        url.append("&page=").append(page);
        url.append("&size=").append(size);

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + CLIENT_ID);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        //ResTemplate를 이용해 요청을 보내고 KakaoSearchDto로 받아 response에 담음
        ResponseEntity<MapResponseDTO.SearchDTO> response = restTemplate.exchange(
                url.toString(),
                HttpMethod.GET,
                entity,
                MapResponseDTO.SearchDTO.class
        );

        // 응답 값 반환
        return response.getBody();
    }

    // 두 지점의 위도와 경도를 기반으로 거리를 계산하는 메서드
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 위도, 경도를 라디안 단위로 변환
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // 위도 차이와 경도 차이 계산
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        // 하버사인 공식 계산
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 계산 (단위: 미터)
        return EARTH_RADIUS * c;
    }

    //    // 장소 검색
//    @Override
//    public MapResponseDTO.ReverseGeocodingDTO reverseGeocoding(String latitude, String longitude) {
//        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="
//                + longitude + "&y=" + latitude;
//
//        // HTTP 요청 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "KakaoAK " + CLIENT_ID);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        //ResTemplate를 이용해 요청을 보내고 KakaoSearchDto로 받아 response에 담음
//        ResponseEntity<MapResponseDTO.ReverseGeocodingDTO> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                MapResponseDTO.ReverseGeocodingDTO.class
//        );
//
//        // 응답 값 반환
//        return response.getBody();
//    }
}
