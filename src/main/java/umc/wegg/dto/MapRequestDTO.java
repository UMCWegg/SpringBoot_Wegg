package umc.wegg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
}
