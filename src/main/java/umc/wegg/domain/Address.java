package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.mapping.MyAddress;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "address")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주소 고유 ID

    private String address; // 주소 이름 (예: 스타벅스 신용산점)
    private float latitude; // 위도
    private float longitude; // 경도

    // Plan과의 관계 설정 (1:N 관계)
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<Plan> plans; // 여러 계획에 연결될 수 있습니다

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyAddress> myAddressList = new ArrayList<>();
}