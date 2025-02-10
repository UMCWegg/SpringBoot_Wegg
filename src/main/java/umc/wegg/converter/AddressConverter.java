package umc.wegg.converter;

import umc.wegg.domain.Address;
import umc.wegg.dto.MapResponseDTO;

public class AddressConverter {

    public static Address toAddress(MapResponseDTO.SearchDTO.PlaceDetailDTO placeDetail){

        Float latitude = Float.parseFloat(placeDetail.getY());
        Float longitude = Float.parseFloat(placeDetail.getX());

        return Address.builder()
                .latitude(latitude)
                .longitude(longitude)
                .placeName(placeDetail.getPlaceName())
                .phone(placeDetail.getPhone())
                .address(placeDetail.getAddressName())
                .roadAddress(placeDetail.getRoadAddressName())
                .placeLabel(placeDetail.getCategoryName())
                .build();
    }
}
