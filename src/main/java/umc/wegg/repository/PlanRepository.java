package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.Plan;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByUserId(Long userId);
    List<Plan> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Plan> findByAddressId(Long addressId);
    // 특정 기간 동안의 일정 검색
//    @Query("SELECT p FROM Plan p WHERE p.startTime >= :start AND p.finishTime <= :end")
//    List<Plan> findPlansBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Plan p WHERE p.user.id = :userId AND p.startTime >= :start AND p.finishTime <= :end")
    List<Plan> findPlansByUserIdBetween(@Param("userId") Long userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

}
