package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 고유 id

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 댓글을 단 사용자

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post; // 댓글이 달릴 게시물

    private String comment; // 댓글 내용

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent; // 부모 댓글 ID (대댓글 기능)
}
