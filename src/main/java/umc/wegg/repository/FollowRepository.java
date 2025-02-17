package umc.wegg.repository;

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

    boolean existsByFollowerAndFolloweeAndFollowStatus(User follower, User followee, FollowStatus status);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee.id = :userId")
    int countFollowers(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    int countFollowing(@Param("userId") Long userId);

    List<Follow> findByFolloweeIdAndFollowStatusOrderByCreatedAtDesc(Long followeeId, FollowStatus followStatus);


    @Query("SELECT f.follower FROM Follow f WHERE f.followee = :postOwner")
    List<User> findFollowersByFollowee(@Param("postOwner") User postOwner);

    boolean existsByFollowerAndFollowee(User follower, User followee);

}
