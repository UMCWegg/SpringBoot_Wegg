package umc.wegg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class MapResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDTO {
        @JsonProperty("documents")
        private List<PlaceDetailDTO> searchByKeywordList;  // 검색 결과 리스트

        @Getter
        @Setter
        @NoArgsConstructor
        public static class PlaceDetailDTO {
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchPlanPlaceListDTO {
        private List<PlaceNameDTO> placeList;

        @Getter
        @AllArgsConstructor
        public static class PlaceNameDTO {
            private String placeName;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchHotPlaceListDTO {
        private List<SearchHotPlaceDTO> placeList;

        @Getter
        @AllArgsConstructor
        public static class SearchHotPlaceDTO {
            private Long addressId;
            private String placeName;
            private String roadAddress;
            private Double distance;
            private Long authCount;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotPlaceListDTO {
        private List<HotPlaceDTO> hotPlaceList;

        @Getter
        @AllArgsConstructor
        public static class HotPlaceDTO {
            private Long addressId;
            private Float latitude;
            private Float longitude;
            private String placeName;
            private String phone;
            private String placeLabel;
            private Long authCount;
            private Long saveCount;
            private Double distance;
            private List<PostDTO> postList = new ArrayList<>();

            @Getter
            @AllArgsConstructor
            public static class PostDTO {
                private Long postId;
                private String imageUrl;
            }
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkDTO {
        private Long myAddressId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnbookmarkDTO {
        private boolean success; // 성공 여부
        private Long myAddressId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailListDTO {
        private List<DetailDTO> detailList;

        @Getter
        @AllArgsConstructor
        public static class DetailDTO {
            private Long addressId;
            private String placeName;
            private Float latitude;
            private Float longitude;
            private Boolean savedStatus;
            private Long authPeople;
            private Long authCount;
            private Long saveCount;
            private String placeLabel;
            private String roadAddress;
            private String phone;
            private List<PostDTO> postList = new ArrayList<>();

            @Getter
            @AllArgsConstructor
            public static class PostDTO {
                private Long postId;
                private String imageUrl;
            }
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkPlaceListDTO {
        private List<BookmarkPlaceDTO> bookmarkPlaceList;

        @Getter
        @AllArgsConstructor
        public static class BookmarkPlaceDTO {
            private Long addressId;
            private Float latitude;
            private Float longitude;
            private String placeName;
            private String placeLabel;
            private Long authCount;
            private Long saveCount;
            private List<PostDTO> postList = new ArrayList<>();

            @Getter
            @AllArgsConstructor
            public static class PostDTO {
                private Long postId;
                private String imageUrl;
            }
        }
    }
}
