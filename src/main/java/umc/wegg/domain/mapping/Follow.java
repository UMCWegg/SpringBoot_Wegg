package umc.wegg.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.User;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.FollowStatus;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "follow")
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 팔로우 관계 고유 ID

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower; // 팔로우를 한 사용자

    @ManyToOne
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee; // 팔로우를 받은 사용자

    // FollowStatus 업데이트를 위한 setter 메서드
    @Setter
    @Enumerated(EnumType.STRING)
    private FollowStatus followStatus; // 팔로우 성공 여부

}
