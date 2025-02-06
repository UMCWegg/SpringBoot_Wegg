package umc.wegg.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MapRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDTO{
        @NotBlank(message = "검색어는 필수 항목입니다.")
        private String keyword;

        private String latitude; //위도

        private String longitude; //경도
    }
}
