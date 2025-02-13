package umc.wegg.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Query(value = """
                SELECT a.id, 
                       CAST((6371 * acos(cos(radians(:centerLat)) * cos(radians(a.latitude)) *
                                         cos(radians(a.longitude) - radians(:centerLon)) +
                                         sin(radians(:centerLat)) * sin(radians(a.latitude)))) AS DOUBLE) AS distance,
                       (SELECT COUNT(p.id) 
                        FROM Posts p 
                        JOIN Plans pl ON p.plan_id = pl.id 
                        WHERE pl.address_id = a.id) AS authCount
                FROM Address a
                WHERE a.longitude BETWEEN :minX AND :maxX 
                  AND a.latitude BETWEEN :minY AND :maxY
                            ORDER BY 
                                CASE WHEN :sortBy = 'distance' THEN distance END ASC,
                                CASE WHEN :sortBy = 'authCount' THEN authCount END DESC
                """,
            countQuery = """
                            SELECT COUNT(*) FROM Address a 
                            WHERE a.longitude BETWEEN :minX AND :maxX 
                              AND a.latitude BETWEEN :minY AND :maxY
                        """,
            nativeQuery = true)
    Page<Object[]> findAddressesWithSorting(
            @Param("minX") double minX,
            @Param("maxX") double maxX,
            @Param("minY") double minY,
            @Param("maxY") double maxY,
            @Param("centerLat") double centerLat,
            @Param("centerLon") double centerLon,
            @Param("sortBy") String sortBy,
            PageRequest pageRequest
    );

    //사용자 위치를 중심으로 특정 거리 내의 주소를 조회하는 쿼리
    @Query(value = """
            SELECT a.*, 
                   (6371 * acos(cos(radians(:userLat)) * cos(radians(a.latitude)) * 
                         cos(radians(a.longitude) - radians(:userLon)) + 
                         sin(radians(:userLat)) * sin(radians(a.latitude)))) AS distance
            FROM Address a
            WHERE (6371 * acos(cos(radians(:userLat)) * cos(radians(a.latitude)) * 
                         cos(radians(a.longitude) - radians(:userLon)) + 
                         sin(radians(:userLat)) * sin(radians(a.latitude)))) <= :maxDistance
              AND a.place_name LIKE %:keyword%
            ORDER BY distance
        """, nativeQuery = true)
    Page<Address> findNearbyAddressesWithKeyword(@Param("userLat") double userLat,
                                                 @Param("userLon") double userLon,
                                                 @Param("maxDistance") double maxDistance,
                                                 @Param("keyword") String keyword,
                                                 PageRequest pageRequest);

}
