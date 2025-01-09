package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "template")
public class Template extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 템플릿 고유 식별자 (Primary Key)

    @Column(nullable = false)
    private String name; // 템플릿 이름 (예: 특정 디자인 또는 형식의 이름)

    @Column(nullable = false)
    private int cost; // 템플릿 비용 (템플릿 구매에 필요한 포인트)

}
