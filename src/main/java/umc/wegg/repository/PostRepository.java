package umc.wegg.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 특정 기간 동안의 게시물 검색
    @Query("SELECT p FROM Post p WHERE p.plan.user.id = :userId AND p.createdAt BETWEEN :start AND :end")
    List<Post> findPostsByUserIdBetween(@Param("userId") Long userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT p.imageUrl FROM Post p WHERE p.plan.id = :planId")
    List<String> findImageUrlsByPlanId(@Param("planId") Long planId);

    @Query("SELECT p FROM Post p JOIN p.plan pl WHERE pl.address.id = :addressId ORDER BY p.createdAt DESC")
    List<Post> findLatestPostsByAddressId(@Param("addressId") Long addressId, PageRequest pageRequest);

    Page<Post> findAll(Pageable pageable);

    List<Post> findByPlanId(Long planId);

    Long countByPlanId(Long planId);

    // 잘못된 save 메서드 제거
}
