package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.ContactFriend;
import umc.wegg.domain.enums.FollowStatus;

import java.util.List;

public interface ContactFriendsRepository extends JpaRepository<ContactFriend, Long> {
    List<ContactFriend> findByUserIdAndIsFollowing(Long userId, FollowStatus isFollowing);

    @Query("SELECT cf FROM ContactFriend cf WHERE cf.user.id = :userId AND cf.isFollowing = :isFollowing ORDER BY cf.id DESC")
    List<ContactFriend> findByUserIdAndIsFollowingOrderByIdDesc(@Param("userId") Long userId, @Param("isFollowing") FollowStatus isFollowing);
}
