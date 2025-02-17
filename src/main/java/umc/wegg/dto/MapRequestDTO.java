package umc.wegg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

public class MapRequestDTO {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchPlanDTO{
        @NotBlank(message = "검색어는 필수 항목입니다.")
        private String keyword;

        private String latitude; //위도

        private String longitude; //경도
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchHotPlaceDTO{
        @NotBlank(message = "검색어는 필수 항목입니다.")
        private String keyword;
        @NotNull
        private Double latitude; //위도
        @NotNull
        private Double longitude; //경도
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewHotPlaceDTO{
        @NotNull
        double minX;   //최소 경도
        @NotNull
        double maxX;   //최대 경도
        @NotNull
        double minY;   //최소 위도
        @NotNull
        double maxY;   //최대 위도

        String sortBy = "distance"; //정렬 기준
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDetailDTO{
        @NotBlank(message = "검색어는 필수 항목입니다.")
        private String placeName;
//        private Double latitude; //위도
//        private Double longitude; //경도
    }
}
