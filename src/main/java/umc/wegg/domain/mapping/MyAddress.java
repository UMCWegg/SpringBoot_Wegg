package umc.wegg.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.Address;
import umc.wegg.domain.Template;
import umc.wegg.domain.User;
import umc.wegg.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "my_address")
public class MyAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 매핑 고유 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 장소를 저장한 사용자

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address; // 저장한 장소
}
