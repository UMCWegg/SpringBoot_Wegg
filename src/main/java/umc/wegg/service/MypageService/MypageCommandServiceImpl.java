package umc.wegg.service.MypageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.MypageConverter;
import umc.wegg.domain.Setting;
import umc.wegg.domain.User;
import umc.wegg.dto.MypageRequestDTO;
import umc.wegg.dto.MypageResponseDTO;
import umc.wegg.repository.SettingRepository;
import umc.wegg.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MypageCommandServiceImpl implements MypageCommandService{

    private final UserRepository userRepository;
    private final SettingRepository settingRepository;
    private MypageConverter mypageConverter;

    public MypageResponseDTO updateSettings(Long userId, MypageRequestDTO.SettingDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Setting setting = settingRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Setting not found"));

        // 변환 로직을 Converter로 이동
        Map<String, Object> updatedFields = mypageConverter.toUpdatedFields(setting, request);

        // 변경된 setting을 저장
        settingRepository.save(setting);

        // 응답 DTO 변환
        return mypageConverter.toResponse(updatedFields);
    }

}
