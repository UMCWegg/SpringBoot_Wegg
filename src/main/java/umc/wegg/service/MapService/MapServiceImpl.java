package umc.wegg.service.MapService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.MyAddressConverter;
import umc.wegg.domain.Address;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Post;
import umc.wegg.domain.User;
import umc.wegg.domain.mapping.MyAddress;
import umc.wegg.dto.MapRequestDTO;
import umc.wegg.dto.MapResponseDTO;
import umc.wegg.repository.*;
import umc.wegg.util.MapUtil;
import umc.wegg.util.RedisUtil;

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
    private final UserRepository userRepository;

    @Override
    public MapResponseDTO.SearchPlanPlaceListDTO searchPlaceListByKeyword(MapRequestDTO.SearchPlanDTO request, Integer page, Integer size) {
        MapResponseDTO.SearchDTO searchPlaces = mapUtil.searchPlacesByKeyword(request.getKeyword(), request.getLatitude(), request.getLongitude(), 2000, page + 1, size);

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

    @Override
    public MapResponseDTO.SearchHotPlaceListDTO searchHotPlaceListByKeyword(MapRequestDTO.SearchHotPlaceDTO request, Integer page, Integer size) {
        Page<Address> addresses = addressRepository.findNearbyAddressesWithKeyword(request.getLatitude(), request.getLongitude(), 2, request.getKeyword(), PageRequest.of(page, size));

        List<MapResponseDTO.SearchHotPlaceListDTO.SearchHotPlaceDTO> placeList = addresses.stream()
                .map(address -> {
                    // 거리 계산
                    double distance = mapUtil.calculateDistance(request.getLatitude(), request.getLongitude(), address.getLatitude(), address.getLongitude());

                    // 게시물 인증 수 계산
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
    public MapResponseDTO.HotPlaceListDTO viewHotPlaceList(MapRequestDTO.ViewHotPlaceDTO request, Integer page, Integer size){

        Sort sort = Sort.by(Sort.Direction.ASC, "distance");

        if ("authCount".equals(request.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, "authCount");
        }

        // DB에서 sortBy에 따라 addressId, distance, authCount 계산해서 가져오기(Paging 적용)
        Page<Object[]> resultPage = addressRepository.findAddressesWithSorting(
                request.getMinX(), request.getMaxX(),
                request.getMinY(), request.getMaxY(),
                (request.getMinX() + request.getMaxX()) / 2,  // 중심 좌표
                (request.getMinY() + request.getMaxY()) / 2,
                PageRequest.of(page, size, sort)
        );

        // DB에서 가져온 값으로 DTO 생성
        MapResponseDTO.HotPlaceListDTO hotPlaceList = new MapResponseDTO.HotPlaceListDTO(
                resultPage.stream()
                        .map(row -> {
                            // DB에서 address, distance, authCount 가져오기
                            Address address = (Address) row[0]; // address
                            Long authCount = (Long) row[1];     // authCount
                            Double distance = (Double) row[2];  //distance

                            // Plan을 거쳐서 Post 목록 가져오기(15개)
                            List<Post> latestPosts = postRepository.findLatestPostsByAddressId(address.getId(), PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdAt")));

                            List<MapResponseDTO.HotPlaceListDTO.HotPlaceDTO.PostDTO> postList = latestPosts.stream()
                                    .map(post -> new MapResponseDTO.HotPlaceListDTO.HotPlaceDTO.PostDTO(
                                            post.getId(), post.getImageUrl()
                                    ))
                                    .collect(Collectors.toList());

                            // 저장된 횟수 계산 (my_address 테이블에서 address_id로 저장된 레코드 수 조회)
                            Long saveCount = myAddressRepository.countByAddressId(address.getId());

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

        return hotPlaceList;
    }

    @Override
    public MapResponseDTO.BookmarkDTO bookmarkAddress(AuthenticatedUser authenticatedUser, Long addressId){

        if (authenticatedUser == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        Long userId = authenticatedUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));


        MyAddress myAddress = MyAddressConverter.toMyAddress(user, address);
        myAddressRepository.save(myAddress);

        return MapResponseDTO.BookmarkDTO.builder()
                .myAddressId(myAddress.getId())
                .build();
    }
}
