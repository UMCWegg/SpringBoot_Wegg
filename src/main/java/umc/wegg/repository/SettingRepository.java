package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Setting;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByUserId(Long userId);
}
