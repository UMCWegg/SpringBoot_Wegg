package umc.wegg.service.MypageService;

import umc.wegg.domain.User;
import umc.wegg.dto.MypageRequestDTO;

public interface MypageCommandService {
    User updateSettings(Long userId, MypageRequestDTO.SettingDTO request);
}
