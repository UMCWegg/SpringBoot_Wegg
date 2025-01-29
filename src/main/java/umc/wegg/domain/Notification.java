package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.domain.enums.ReadStatus;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "user")
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림의 고유 식별자 (Primary Key)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림을 받은 사용자 (users 테이블과 연관 관계 - Foreign Key)

    private String content;
    private String url;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; // 알림의 유형 (enum: FRIEND_REQUEST, ALERT 등, 데이터베이스에 문자열로 저장)

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus; // 알림 읽음 상태 (enum: READ, UNREAD 등)

    @Builder
    public Notification(User user, NotificationType notificationType, String content, String url, ReadStatus readStatus) {
        this.user = user;
        this.notificationType = notificationType;
        this.content = content;
        this.url = url;
        this.readStatus = (readStatus != null) ? readStatus : ReadStatus.UNREAD;
    }

    public String getReceiverName() {
        return user.getName();
    } // receiver의 name을 받아올지 accoutId를 받아올지는 pm님께 여쭤보자


}
