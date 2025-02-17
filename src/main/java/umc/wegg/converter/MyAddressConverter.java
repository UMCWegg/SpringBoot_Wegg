package umc.wegg.converter;

import umc.wegg.domain.Address;
import umc.wegg.domain.User;
import umc.wegg.domain.mapping.MyAddress;

public class MyAddressConverter {

    public static MyAddress toMyAddress(User user, Address address) {
        return MyAddress.builder()
                .user(user)
                .address(address)
                .build();
    }
}
