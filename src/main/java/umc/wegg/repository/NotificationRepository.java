package umc.wegg.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Notification;
import umc.wegg.domain.enums.ReadStatus;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 알림을 사용자별로 조회하는 메소드 추가 가능
    List<Notification> findByUserId(Long user);
}
