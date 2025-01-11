package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}