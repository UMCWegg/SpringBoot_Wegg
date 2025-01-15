package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Post;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.EmojiType;
import umc.wegg.domain.mapping.Emoji;

import java.util.Optional;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
    boolean existsByPostAndUserAndType(Post post, User user, EmojiType type);

    Optional<Emoji> findByPostAndUserAndType(Post post, User user, EmojiType type);
    // 필요 시 커스텀 메서드 추가
}

