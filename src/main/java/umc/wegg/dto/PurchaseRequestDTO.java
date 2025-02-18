package umc.wegg.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.wegg.domain.enums.TemplateType;

public class PurchaseRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class TemplatePurchaseRequestDTO {
        private TemplateType templateType; // 구매할 템플릿 타입 (ONE~NINE)
    }

    @Getter
    @NoArgsConstructor
    public static class AddPointsRequestDTO {
        private int pointsToAdd; // 충전할 포인트 값
    }

}
