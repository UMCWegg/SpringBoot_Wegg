package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Template;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

}
