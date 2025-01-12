package umc.wegg.service.MypageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.User;
import umc.wegg.dto.MypageRequestDTO;
import umc.wegg.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MypageCommandServiceImpl implements MypageCommandService{

    private final UserRepository userRepository;

    @Override
    public User editMypage(Long userId, MypageRequestDTO.EditDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 변경된 값만 업데이트
        if (request.getAccountId() != null) {
            user.setAccountId(request.getAccountId());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        return userRepository.save(user); // 업데이트된 User 저장
    }

    @Override
    public User updateSettings(Long userId, MypageRequestDTO.SettingDTO request) {
//        // User 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Setting을 별도로 조회
//        Setting setting = settingRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Setting not found"));
//
//        // 요청된 값들로 설정을 업데이트
//        if (request.getPostAlarm() != null) {
//            setting.setPostAlarm(request.getPostAlarm());
//        }
//        if (request.getCommentAlarm() != null) {
//            setting.setCommentAlarm(request.getCommentAlarm());
//        }
//        if (request.getPlaceAlarm() != null) {
//            setting.setPlaceAlarm(request.getPlaceAlarm());
//        }
//        if (request.getRandomAlarm() != null) {
//            setting.setRandomAlarm(request.getRandomAlarm());
//        }
//        if (request.getEggAlarm() != null) {
//            setting.setEggAlarm(request.getEggAlarm());
//        }
//
//        setting.setMarketingAgree(request.isMarketingAgree());
//        setting.setPlaceCheck(request.isPlaceCheck());
//        setting.setRandomCheck(request.isRandomCheck());
//        if (request.getAccountVisibility() != null) {
//            setting.setAccountVisibility(request.getAccountVisibility());
//        }
//
//        // 변경된 setting을 저장
//        settingRepository.save(setting);
//
//        // User는 별도로 수정하지 않으므로 그대로 반환
        return null;
    }

}
