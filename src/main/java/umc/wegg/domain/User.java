package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.AccountVisibility;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID

    @Column(nullable = false)
    private String email; // 사용자 이메일

    @Column(nullable = false, length = 10)
    private String accountId;  // 사용자 계정 ID

    @Column(nullable = false)
    private String password;  // 비밀번호

    private LocalDate inactiveDate; // 비활성 기간 (null 값이라면 활동 중, 데이터가 존재하면 휴면 중 / 회원 탈퇴 시 휴면 처리해야 함)

    private String name; // 사용자 이름
    private String profileImage; // 프로필 이미지 URL
    private int points; // 포인트
    private int successCount; // 연속 인증 성공 횟수

    private float currentLat; // 현재 위치 위도
    private float currentLon; // 현재 위치 경도

    private String job; // 사용자 신분
    private String reason; // 이 앱을 시작한 이유

    @Column(nullable = false, length = 100)
    private String phone; // 사용자 전화번호

    @Enumerated(EnumType.STRING)
    private AccountVisibility accountVisibility; // 계정 공개 여부 (전체 공개, 맞팔만 공개)
}
