package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByPlaceName(String placeName);

    // 위도와 경도 범위에 맞는 주소만 조회하는 쿼리
    @Query("SELECT a FROM Address a WHERE a.longitude BETWEEN :minX AND :maxX AND a.latitude BETWEEN :minY AND :maxY")
    List<Address> findAddressesInRange(@Param("minX") double minX,
                                       @Param("maxX") double maxX,
                                       @Param("minY") double minY,
                                       @Param("maxY") double maxY);

    //사용자 위치를 중심으로 특정 거리 내의 주소를 조회하는 쿼리
    @Query("SELECT a FROM Address a WHERE (6371 * acos(" +
            "cos(radians(:userLat)) * cos(radians(a.latitude)) * cos(radians(a.longitude) - radians(:userLon)) + " +
            "sin(radians(:userLat)) * sin(radians(a.latitude)))) <= :maxDistance " +
            "AND a.placeName LIKE %:keyword%")
    List<Address> findNearbyAddressesWithKeyword(@Param("userLat") double userLat,
                                                 @Param("userLon") double userLon,
                                                 @Param("maxDistance") double maxDistance,
                                                 @Param("keyword") String keyword);

}
