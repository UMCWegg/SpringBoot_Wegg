package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Egg;

import java.util.Optional;

public interface EggRepository extends JpaRepository<Egg, Long> {
    Optional<Egg> findByPlanId(Long planId);  // Plan ID로 Egg 찾기
}
