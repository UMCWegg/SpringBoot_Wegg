package umc.wegg.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.Post;
import umc.wegg.domain.User;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.EmojiType;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "emojis")
public class Emoji extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이모지 고유 id

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 이모지 남긴 사용자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmojiType type; // 이모지 유형 (LIKE, THUMBS_UP, LAUGH)

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 이모지가 남겨진 게시물
}
