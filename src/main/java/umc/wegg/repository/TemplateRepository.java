package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Template;
import umc.wegg.domain.enums.TemplateType;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByTemplateType(TemplateType templateType);
}
