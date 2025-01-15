package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Egg;

public interface EggRepository extends JpaRepository<Egg, Long> {
}
