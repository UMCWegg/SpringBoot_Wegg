package umc.wegg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.wegg.domain.enums.EggStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EggRequestDTO { // 알 깨기
    //    @NotNull(message = "상태 값은 필수 입력 항목입니다.")
    //    @Schema(description = "계란 상태 (BREAK)", example = "BREAK")
    //    private EggStatus status; // 상태 (BREAK, INTACT)
    @NotNull(message = "알을 깬 사용자는 필수 입력 항목입니다.")
    @Schema(description = "알을 깬 사용자 ID", example = "1")
    private Long breakerId; // 알을 깬 사용자 ID
}
