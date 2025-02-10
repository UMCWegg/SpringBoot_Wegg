package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.wegg.domain.mapping.MyAddress;

public interface MyAddressRepository extends JpaRepository<MyAddress, Long> {
    Long countByAddressId(Long addressId);
}
