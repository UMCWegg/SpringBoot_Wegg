package umc.wegg.service.MapService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.Address;
import umc.wegg.domain.Plan;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.repository.AddressRepository;
import umc.wegg.repository.MyAddressRepository;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.PostRepository;
import umc.wegg.util.MapUtil;
import umc.wegg.util.RedisUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final AddressRepository addressRepository;
    private final MyAddressRepository myAddressRepository;
    private final PostRepository postRepository;
    private final PlanRepository planRepository;

    private final MapUtil mapUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper; // JSON 직렬화/역직렬화에 사용

    @Override
    public MapResponseDTO.SearchPlanPlaceListDTO searchPlaceListByKeyword(MapRequestDTO.SearchPlanDTO request) {
        MapResponseDTO.SearchDTO searchPlaces = mapUtil.searchPlacesByKeyword(request.getKeyword(), request.getLatitude(), request.getLongitude(), 2000);

        // PlaceListDTO 생성 및 검색된 장소 이름 추가
        List<MapResponseDTO.SearchPlanPlaceListDTO.PlaceNameDTO> placeList = searchPlaces.getSearchByKeywordList().stream()
                .map(place -> new MapResponseDTO.SearchPlanPlaceListDTO.PlaceNameDTO(place.getPlaceName())) // 장소 이름 DTO로 변환
                .collect(Collectors.toList());

        // Redis에 검색 결과 저장
        for (MapResponseDTO.SearchDTO.PlaceDetailDTO place : searchPlaces.getSearchByKeywordList()) {
            try {
                String key = place.getPlaceName();
                String value = objectMapper.writeValueAsString(place); // place detail
                redisUtil.setDataExpire(key, value, 10 * 60); // Redis에 저장(key-place name/value-place detail)
            } catch (Exception e) {
                e.printStackTrace(); // 직렬화 실패 시 예외 처리
            }
        }

        // PlaceListDTO 생성
        return MapResponseDTO.SearchPlanPlaceListDTO.builder()
                .placeList(placeList)
                .build();

    }

    public MapResponseDTO.SearchHotPlaceListDTO searchHotPlaceListByKeyword(MapRequestDTO.SearchHotPlaceDTO request) {
        List<Address> addresses = addressRepository.findNearbyAddressesWithKeyword(request.getLatitude(), request.getLongitude(), 2, request.getKeyword());

        List<MapResponseDTO.SearchHotPlaceListDTO.SearchHotPlaceDTO> placeList = addresses.stream()
                .map(address -> {
                    // 거리 계산
                    double distance = mapUtil.calculateDistance(request.getLatitude(), request.getLongitude(), address.getLatitude(), address.getLongitude());

                    // 게시물 인증 수 계산 (예시로 postRepository.countByPlanId 사용)
                    List<Plan> plans = planRepository.findByAddressId(address.getId()); // Address ID로 Plan 조회
                    Long authCount = plans.stream()
                            .mapToLong(plan -> postRepository.countByPlanId(plan.getId())) // 각 Plan에 대한 Post 개수 세기
                            .sum(); // 해당 address_id에 대한 모든 Plan에 연결된 Post 개수 합산

                    // SearchHotPlaceDTO 객체 생성
                    return new MapResponseDTO.SearchHotPlaceListDTO.SearchHotPlaceDTO(
                            address.getId(),
                            address.getPlaceName(),
                            address.getRoadAddress(),
                            distance,
                            authCount
                    );
                })
                .collect(Collectors.toList());

        // SearchHotPlaceListDTO 객체 반환
        return MapResponseDTO.SearchHotPlaceListDTO.builder()
                .placeList(placeList)
                .build();
    }

    @Override
    public MapResponseDTO.HotPlaceListDTO viewHotPlaceList(double minX, double maxX, double minY, double maxY, String sortBy){
        List<Address> addressList = addressRepository.findAddressesInRange(minX, maxX, minY, maxY);

        // 1. 범위 안에 있는 주소 필터링
        MapResponseDTO.HotPlaceListDTO hotPlaceList = new MapResponseDTO.HotPlaceListDTO(
                addressList.stream()
                        .map(address -> {
                            // Plan을 거쳐서 Post 개수 계산 (Address -> Plan -> Post)
                            List<Plan> plans = planRepository.findByAddressId(address.getId()); // Address ID로 Plan 조회
                            Long authCount = plans.stream()
                                    .mapToLong(plan -> postRepository.countByPlanId(plan.getId())) // 각 Plan에 대한 Post 개수 세기
                                    .sum(); // 해당 address_id에 대한 모든 Plan에 연결된 Post 개수 합산

                            // Plan을 거쳐서 Post 목록 가져오기 (Post 가져오기)
                            List<MapResponseDTO.HotPlaceListDTO.HotPlaceDTO.PostDTO> postList = plans.stream()
                                    .flatMap(plan -> postRepository.findByPlanId(plan.getId()).stream()) // Plan을 거쳐서 Post 가져오기
                                    .map(post -> new MapResponseDTO.HotPlaceListDTO.HotPlaceDTO.PostDTO(
                                            post.getId(),    // Post의 ID
                                            post.getImageUrl() // Post의 이미지 URL
                                    ))
                                    .collect(Collectors.toList());

                            // 2.3 saveCount 계산 (my_address 테이블에서 address_id로 저장된 레코드 수 조회)
                            Long saveCount = myAddressRepository.countByAddressId(address.getId());

                            double centerLon = (minX + maxX) / 2;  // 경도 중심값
                            double centerLat = (minY + maxY) / 2;  // 위도 중심값

                            Double distance = mapUtil.calculateDistance(centerLat, centerLon, address.getLatitude(), address.getLongitude()); // 거리 계산

                            return new MapResponseDTO.HotPlaceListDTO.HotPlaceDTO(
                                    address.getId(),
                                    address.getLatitude(),
                                    address.getLongitude(),
                                    address.getPlaceName(),
                                    address.getPlaceLabel(),
                                    authCount,
                                    saveCount,
                                    distance,
                                    postList
                            );
                        })
                        .collect(Collectors.toList()) // List<HotPlaceDTO> 생성
        );

        // 정렬
        if ("distance".equals(sortBy)) {
            hotPlaceList.getHotPlaceList().sort(Comparator.comparingDouble(MapResponseDTO.HotPlaceListDTO.HotPlaceDTO::getDistance));
        } else if ("authCount".equals(sortBy)) {
            hotPlaceList.getHotPlaceList().sort(Comparator.comparingLong(MapResponseDTO.HotPlaceListDTO.HotPlaceDTO::getAuthCount).reversed());
        }

        return hotPlaceList;
    }

}
