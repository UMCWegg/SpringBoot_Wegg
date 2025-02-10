package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.AlarmType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "settings")
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 설정 고유 id

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 해당 설정을 한 사용자

    @Enumerated(EnumType.STRING)
    private AlarmType postAlarm; // 친구들 포스트 알림

    @Enumerated(EnumType.STRING)
    private AlarmType commentAlarm; // 댓글 알림

    @Enumerated(EnumType.STRING)
    private AlarmType placeAlarm; // 장소 인증 알림

    @Enumerated(EnumType.STRING)
    private AlarmType randomAlarm; // 랜덤 인증 알림

    @Enumerated(EnumType.STRING)
    private AlarmType eggAlarm; // 알 깨기 알림

    private boolean marketingAgree; // 마케팅 동의
    private boolean placeCheck; // 장소 인증 기능 on/off
    private boolean randomCheck; // 랜덤 인증 기능 on/off
    private boolean breakAllow; // 알 깨기 기능 (아예 끄기/맞팔만)

    @Enumerated(EnumType.STRING)
    private AccountVisibility accountVisibility; // 계정 공개 범위 (전체공개/맞팔만)
}
