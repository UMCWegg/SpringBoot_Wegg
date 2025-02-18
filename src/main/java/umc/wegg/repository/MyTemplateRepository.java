package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Template;
import umc.wegg.domain.User;
import umc.wegg.domain.mapping.MyTemplate;
import java.util.List;

public interface MyTemplateRepository extends JpaRepository<MyTemplate, Long> {
    List<MyTemplate> findByUserId(Long userId);

    boolean existsByUserAndTemplate(User user, Template template);
}
