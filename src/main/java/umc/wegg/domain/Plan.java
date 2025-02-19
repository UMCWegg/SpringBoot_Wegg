package umc.wegg.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.LateStatus;
import umc.wegg.domain.enums.PlanStatus;

import java.time.LocalDate;
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
    private PlanStatus status; // 계획 상태(YET, SUCCEEDED, FAILED)

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime; // 공부 시작 시간 (랜덤 인증 시작 시간)
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime finishTime; // 공부 종료 시간 (랜덤 인증 종료 시간)
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime randomTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LateStatus lateTime; // 지각 허용 시간

    private LocalDate planDate; // 반복 대신 날짜 설정

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false) // 외래 키로 Address 연결
    private Address address; // 공부할 위치의 주소 정보

    private Boolean planOn; // 계획 온오프

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 순환 참조 방지
    private Egg egg; // 알과의 1:1 관계
}