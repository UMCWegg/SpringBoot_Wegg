package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.Address;
import umc.wegg.domain.Comment;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
