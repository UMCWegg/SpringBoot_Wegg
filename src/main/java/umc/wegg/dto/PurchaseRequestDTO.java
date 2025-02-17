package umc.wegg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    @Schema(description = "구매할 포인트", example = "30", defaultValue = "30")
    private int points;
}
