package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.PlanStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "egg")
public class Egg extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알의 고유 식별자 (Primary Key)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status; // 알의 상태 (enum: CREATED, BROKEN 등 상태값 정의)

    private LocalDateTime brokenTime; // 알이 깨진 시간 (nullable=false, 알이 깨졌을 때 기록)

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan; // 알이 연결된 공부 계획 (plans 테이블과 연관 관계 - Foreign Key)

    @ManyToOne
    @JoinColumn(name = "breaker_id", nullable = false)
    private User user; // 알을 깬 사용자 (users 테이블과 연관 관계 - Foreign Key)

}
