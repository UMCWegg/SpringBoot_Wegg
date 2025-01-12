package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

}
