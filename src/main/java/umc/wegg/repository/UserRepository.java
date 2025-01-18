package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.User;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByAccountId(String accountId);

    // 인증 성공 횟수 계산
//    @Query("SELECT u.successCount FROM User u WHERE u.id = :userId")
//    int findSuccessCount();

    @Query("SELECT u.successCount FROM User u WHERE u.id = :userId")
    int findSuccessCountByUserId(@Param("userId") Long userId);

}
