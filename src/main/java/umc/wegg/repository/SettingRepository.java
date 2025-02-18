package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.Setting;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByUserId(Long userId);

//    @Query("SELECT s FROM Setting s WHERE s.user.id = :userId")
//    Optional<Setting> findByUserId(@Param("userId") Long userId);
}
