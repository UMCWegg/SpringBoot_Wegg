package umc.wegg.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.enums.ReplayStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "plans")
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 계획 고유 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 계획 작성자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status; // 계획 상태(YET,SUCCEEDED,FAILED)

    private float latitude; // 공부할 위치 위도
    private float longitude; // 공부할 위치 경도

    private LocalDateTime startTime; // 공부 시작 시간 (랜덤 인증 시작 시간)
    private LocalDateTime finishTime; // 공부 종료 시간 (랜덤 인증 종료 시간)

    private int lateTime; // 지각 허용 시간

    @Enumerated(EnumType.STRING)
    private ReplayStatus replay; // 반복 -> 아이폰 알람 반복 참고하는 걸로 이해하고 enum 만들었음.

    private String address; // 공부할 위치의 이름 ex) 스타벅스 신용산점

    private LocalDateTime date; // 계획의 날짜

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 순환 참조 방지
    private Egg egg; // 알과의 1:1 관계
}
