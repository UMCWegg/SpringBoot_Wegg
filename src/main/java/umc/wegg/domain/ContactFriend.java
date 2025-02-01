package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//user_id와 phone_num 조합이 유일하도록 UNIQUE 제약 조건 추가
@Table(name = "contact_friends", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "phone_num"})
})
public class ContactFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 전략
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // User와 다대일 관계
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 매핑
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // User와 다대일 관계
    @JoinColumn(name = "friend_id", nullable = false) // 외래 키 매핑
    private User friend;

    @Column(name = "phone_num", nullable = false, length = 255)
    private String phoneNum;

    @Column(name = "is_following", nullable = false, length = 255)
    private Boolean isFollowing;
}

