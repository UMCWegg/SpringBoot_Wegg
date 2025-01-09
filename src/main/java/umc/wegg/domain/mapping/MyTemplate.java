package umc.wegg.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import umc.wegg.domain.Template;
import umc.wegg.domain.User;
import umc.wegg.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "my_template")
public class MyTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 매핑 고유 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 템플릿을 소유한 사용자

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private Template template; // 소유한 템플릿
}
