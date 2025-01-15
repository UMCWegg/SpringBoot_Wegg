package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 기본적인 CRUD 메서드와 필요하면 커스텀 쿼리 추가
}

