package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.ReadStatus;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림의 고유 식별자 (Primary Key)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림을 받은 사용자 (users 테이블과 연관 관계 - Foreign Key)

    private String type; // 알림의 유형 (enum: FRIEND_REQUEST, ALERT 등, 데이터베이스에 문자열로 저장)

    private String title; // 알림 제목 (최대 100자)

    private String message; // 알림 내용 (최대 255자)

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus; // 알림 읽음 상태 (enum: READ, UNREAD 등)

}
