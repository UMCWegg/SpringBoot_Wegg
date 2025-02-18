package umc.wegg.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.mapping.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findById(Long id);

    //웨이팅 개수 세기
    Long countByFolloweeIdAndFollowStatus(Long followeeId, FollowStatus followStatus);
    //한명의 어카운트아이디 가져오기
    @Query("SELECT f.follower.accountId FROM Follow f WHERE f.followee.id = :followeeId ORDER BY f.updatedAt DESC LIMIT 1")
    Optional<String> findLatestFollowerAccountIdByFolloweeId(@Param("followeeId") Long followeeId);

    boolean existsByFollowerAndFolloweeAndFollowStatus(User follower, User followee, FollowStatus status);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee.id = :userId")
    int countFollowers(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    int countFollowing(@Param("userId") Long userId);

    List<Follow> findByFolloweeIdAndFollowStatusOrderByCreatedAtDesc(Long followeeId, FollowStatus followStatus);


    @Query("SELECT f.follower FROM Follow f WHERE f.followee = :postOwner")
    List<User> findFollowersByFollowee(@Param("postOwner") User postOwner);

    boolean existsByFollowerAndFollowee(User follower, User followee);

    @Query("SELECT f.followee, COUNT(f.follower) AS followCount " +
            "FROM Follow f WHERE f.follower IN " +
            "(SELECT f2.follower FROM Follow f2 WHERE f2.followee.id = :userId) " +
            "AND f.followee.id <> :userId " +
            "GROUP BY f.followee " +
            "ORDER BY followCount DESC")
    List<Object[]> findRecommendedUsersWithFollowCount(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f.followee FROM Follow f GROUP BY f.followee ORDER BY COUNT(f.follower) DESC")
    List<User> findTopFollowedUsers(Pageable pageable);


    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);
}
