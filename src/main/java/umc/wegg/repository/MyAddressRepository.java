package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.mapping.MyAddress;

import java.util.Optional;

public interface MyAddressRepository extends JpaRepository<MyAddress, Long> {
    Long countByAddressId(Long addressId);

    Optional<MyAddress> findByUserIdAndAddressId(Long userId, Long addressId);
}
