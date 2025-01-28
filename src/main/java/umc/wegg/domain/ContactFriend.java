package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "contact_friends")
public class ContactFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 전략
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // User와 다대일 관계
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 매핑
    private User user;

    @Column(name = "phone_num", nullable = false, length = 255)
    private String phoneNum;
}

