package umc.wegg.service.MypageService;

import umc.wegg.domain.User;
import umc.wegg.dto.MypageRequestDTO;

public interface MypageCommandService {
    User editMypage(Long userId, MypageRequestDTO.EditDTO request);
    User updateSettings(Long userId, MypageRequestDTO.SettingDTO request);
}
