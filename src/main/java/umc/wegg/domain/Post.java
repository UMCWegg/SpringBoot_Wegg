package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "posts")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시물 고유 id

    @Column(nullable = false)
    private String imageUrl; // 게시물 이미지 URL

    @Column(nullable = false)
    private String comment; // 게시물 코멘트

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template; // 게시물의 사용된 템플릿

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan; // 연결된 계획
}
