package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Time;

public interface TimeRepository extends JpaRepository<Time, Long> {
}
