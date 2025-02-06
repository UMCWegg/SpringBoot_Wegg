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

    //키워드로 장소 검색하기 API(kakao)
    public MapResponseDTO.SearchDTO searchPlacesByKeyword(String keyword, String latitude, String longitude, Integer radius) {
        StringBuilder url = new StringBuilder("https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword);

        // latitude, longitude, radius가 null이 아닐 때만 추가
        if (longitude != null && latitude != null) {
            url.append("&x=").append(longitude).append("&y=").append(latitude);
        }
        if (radius != null) {
            url.append("&radius=").append(radius);
        }

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
