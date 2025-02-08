package umc.wegg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeRequestDTO {  // 시간 기록

    @NotNull(message = "지속 시간은 필수 입력 항목입니다.")
    @Schema(description = "지속 시간 (분 단위)", example = "90")
    private Integer duration;

//    @NotNull(message = "사용자 ID는 필수 입력 항목입니다.")
//    @Schema(description = "사용자 ID", example = "1")
//    private Long userId; // 사용자 ID
}
