package umc.wegg.converter;

import org.jetbrains.annotations.NotNull;
import umc.wegg.domain.Address;
import umc.wegg.dto.MapResponseDTO;

public class AddressConverter {

    public static Address toAddress(MapResponseDTO.SearchDTO.PlaceDetailDTO placeDetail){

        Float latitude = Float.parseFloat(placeDetail.getY());
        Float longitude = Float.parseFloat(placeDetail.getX());

        String placeLabel = getPlaceLabel(placeDetail.getCategoryName());

        return Address.builder()
                .latitude(latitude)
                .longitude(longitude)
                .placeName(placeDetail.getPlaceName())
                .phone(placeDetail.getPhone())
                .address(placeDetail.getAddressName())
                .roadAddress(placeDetail.getRoadAddressName())
                .placeLabel(placeLabel)
                .build();
    }

    @NotNull
    private static String getPlaceLabel(String categoryName) {
        String placeLabel = "기타"; // 기본적으로 categoryName 값을 사용

        if (categoryName.contains("스터디카페")) {
            placeLabel = "스터디카페";
        } else if (categoryName.contains("카페")) {
            placeLabel = "카페";
        } else if (categoryName.contains("도서관")) {
            placeLabel = "도서관";
        } else if (categoryName.contains("독서실")) {
            placeLabel = "독서실";
        } else if (categoryName.contains("학교")) {
            placeLabel = "학교";
        } else if (categoryName.contains("주거시설")) {
            placeLabel = "주거시설";
        } else if (categoryName.contains("학원")) {
            placeLabel = "학원";
        }
        return placeLabel;
    }
}
