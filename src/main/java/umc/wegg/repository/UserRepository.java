package umc.wegg.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByAccountId(String accountId);

    // 인증 성공 횟수 계산
//    @Query("SELECT u.successCount FROM User u WHERE u.id = :userId")
//    int findSuccessCount();

    //유저의 계정공개범위 가져오기
    @Query("SELECT s.accountVisibility FROM User u JOIN u.setting s WHERE u.id = :userId")
    String findAccountVisibilityByUserId(@Param("userId") Long userId);

    @Query("SELECT u.successCount FROM User u WHERE u.id = :userId")
    int findSuccessCountByUserId(@Param("userId") Long userId);

    boolean existsByAccountId(String accountId);
    boolean existsByEmail(String email);
    // 📌 가장 최근에 포인트를 받은 successCount 조회
    @Query("SELECT u.lastReceivedSuccessCount FROM User u WHERE u.id = :userId")
    Optional<Integer> findLastReceivedSuccessCount(@Param("userId") Long userId);

    // 📌 lastReceivedSuccessCount 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastReceivedSuccessCount = :successCount WHERE u.id = :userId")
    void updateLastReceivedSuccessCount(@Param("userId") Long userId, @Param("successCount") int successCount);

    @Query("SELECT u FROM User u WHERE u.accountId LIKE %?1%")
    List<User> findByAccountIdContaining(String keyword);

    @Query("SELECT u FROM User u ORDER BY FUNCTION('RAND')")
    List<User> findRandomUsers(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u")
    Long countTotalUsers();
}
