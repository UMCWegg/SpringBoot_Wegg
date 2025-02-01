package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Uuid;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
