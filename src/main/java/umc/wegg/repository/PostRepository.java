package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 기본적인 CRUD 메서드와 필요하면 커스텀 쿼리 추가

    // 특정 기간 동안의 게시물 검색
//    @Query("SELECT p FROM Post p WHERE p.createdAt >= :start AND p.createdAt <= :end")
//    List<Post> findPostsBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Post p WHERE p.plan.user.id = :userId AND p.createdAt >= :start AND p.createdAt <= :end")
    List<Post> findPostsByUserIdBetween(@Param("userId") Long userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}

