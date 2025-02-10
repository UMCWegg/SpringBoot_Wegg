package umc.wegg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

public class MapResponseDTO {

//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ReverseGeocodingDTO {
//        @JsonProperty("documents")
//        private List<AddressDTO> reverseGeocodingist;  // 검색 결과 리스트
//
//        @Getter
//        @Setter
//        @NoArgsConstructor
//        public static class AddressDTO {
//            @JsonProperty("road_address")
//            private RoadAddress roadAddress; // 도로명 주소
//
//            @JsonProperty("address")
//            private Address address; // 지번 주소
//        }
//
//        @Getter
//        @Setter
//        @NoArgsConstructor
//        public static class RoadAddress {
//            @JsonProperty("address_name")
//            private String addressName;
//
//            @JsonProperty("region_1depth_name")
//            private String region1DepthName;
//
//            @JsonProperty("region_2depth_name")
//            private String region2DepthName;
//
//            @JsonProperty("region_3depth_name")
//            private String region3DepthName;
//
//            @JsonProperty("road_name")
//            private String roadName;
//
//            @JsonProperty("underground_yn")
//            private String undergroundYn;
//
//            @JsonProperty("main_building_no")
//            private String mainBuildingNo;
//
//            @JsonProperty("sub_building_no")
//            private String subBuildingNo;
//
//            @JsonProperty("building_name")
//            private String buildingName;
//
//            @JsonProperty("zone_no")
//            private String zoneNo; // 우편번호
//        }
//
//        @Getter
//        @Setter
//        @NoArgsConstructor
//        public static class Address {
//            @JsonProperty("address_name")
//            private String addressName;
//
//            @JsonProperty("region_1depth_name")
//            private String region1DepthName;
//
//            @JsonProperty("region_2depth_name")
//            private String region2DepthName;
//
//            @JsonProperty("region_3depth_name")
//            private String region3DepthName;
//
//            @JsonProperty("mountain_yn")
//            private String mountainYn;
//
//            @JsonProperty("main_address_no")
//            private String mainAddressNo;
//
//            @JsonProperty("sub_address_no")
//            private String subAddressNo;
//
//            @JsonProperty("zip_code")
//            private String zipCode; // 우편번호
//        }
//    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDTO {
        @JsonProperty("documents")
        private List<SearchByKeywordDTO> searchByKeywordList;  // 검색 결과 리스트

        @Getter
        @Setter
        @NoArgsConstructor
        public static class SearchByKeywordDTO {
            private String id;               //장소 ID
            @JsonProperty("place_name")
            private String placeName;       // 장소 이름
            @JsonProperty("address_name")
            private String addressName;     // 지번 주소
            @JsonProperty("road_address_name")
            private String roadAddressName; // 도로명 주소
            private String x;                // 경도(Longitude)
            private String y;                // 위도(Latitude)
            private String phone;            // 전화번호
            @JsonProperty("category_name")
            private String categoryName;    // 카테고리 정보
        }
    }
}
