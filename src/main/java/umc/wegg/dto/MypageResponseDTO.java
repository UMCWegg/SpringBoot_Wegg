package umc.wegg.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
public class MypageResponseDTO {

    private String message;  // 응답 메시지 (ex. "설정이 성공적으로 변경되었습니다.")
    private Map<String, Object> updatedFields; // 변경된 필드만 포함

}
