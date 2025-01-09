package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "time")
public class Time extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 시간 고유 id

    @Column(nullable = false)
    private LocalDateTime date; // 시간이 기록될 날짜

    @Column(nullable = false)
    private Long duration; // 공부 시간

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 공부한 사용자
}

