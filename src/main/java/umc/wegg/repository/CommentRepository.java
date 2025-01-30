package umc.wegg.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Comment;
import umc.wegg.domain.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 커스텀 메서드 예: 특정 Post의 모든 댓글 가져오기
    List<Comment> findByPostId(Long postId);

    List<Comment> findByPost(Post post); //페이징 적용안함. 아직
    Page<Comment> findByPost(Post post, Pageable pageable);
}

