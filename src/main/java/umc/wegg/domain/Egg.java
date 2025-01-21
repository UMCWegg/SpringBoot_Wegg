package umc.wegg.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.EggStatus;
import umc.wegg.domain.enums.PlanStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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
    private EggStatus status; // 알의 상태 (enum: INTACT, BROKEN 등 상태값 정의)

    private LocalDateTime brokenTime; // 알이 깨진 시간 (상태가 BROKEN일 때 기록)

    @OneToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnore // 순환 참조 방지
    private Plan plan; // 연결된 계획 (Plan과 1:1 관계)

    @ManyToOne
    @JoinColumn(name = "breaker_id")
    private User user; // 알을 깬 사용자 (알이 깨졌을 때만 값이 있음)

}
