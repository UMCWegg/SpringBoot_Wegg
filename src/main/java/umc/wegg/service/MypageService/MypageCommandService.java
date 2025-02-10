package umc.wegg.service.MypageService;

import umc.wegg.domain.User;
import umc.wegg.dto.MypageRequestDTO;
import umc.wegg.dto.MypageResponseDTO;

public interface MypageCommandService {
    MypageResponseDTO updateSettings(Long userId, MypageRequestDTO.SettingDTO request);
}
