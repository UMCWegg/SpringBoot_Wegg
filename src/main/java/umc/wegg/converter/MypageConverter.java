package umc.wegg.converter;

import umc.wegg.domain.Setting;
import umc.wegg.dto.MypageRequestDTO;
import umc.wegg.dto.MypageResponseDTO;

import java.util.HashMap;
import java.util.Map;

public class MypageConverter {
    public static Map<String, Object> toUpdatedFields(Setting setting, MypageRequestDTO.SettingDTO request) {
        Map<String, Object> updatedFields = new HashMap<>();


        setting.setPostAlarm(request.isPostAlarm());
        updatedFields.put("postAlarm", request.isPostAlarm());

        setting.setCommentAlarm(request.isCommentAlarm());
        updatedFields.put("commentAlarm", request.isCommentAlarm());

        if (request.getPlaceAlarm() != null) {
            setting.setPlaceAlarm(request.getPlaceAlarm());
            updatedFields.put("placeAlarm", request.getPlaceAlarm());
        }
        if (request.getRandomAlarm() != null) {
            setting.setRandomAlarm(request.getRandomAlarm());
            updatedFields.put("randomAlarm", request.getRandomAlarm());
        }
        if (request.getEggAlarm() != null) {
            setting.setEggAlarm(request.getEggAlarm());
            updatedFields.put("eggAlarm", request.getEggAlarm());
        }

        setting.setMarketingAgree(request.isMarketingAgree());
        updatedFields.put("marketingAgree", request.isMarketingAgree());

        setting.setPlaceCheck(request.isPlaceCheck());
        updatedFields.put("placeCheck", request.isPlaceCheck());

        setting.setRandomCheck(request.isRandomCheck());
        updatedFields.put("randomCheck", request.isRandomCheck());

        setting.setBreakAllow(request.isBreakAllow());
        updatedFields.put("breakAllow", request.isBreakAllow());

        if (request.getAccountVisibility() != null) {
            setting.setAccountVisibility(request.getAccountVisibility());
            updatedFields.put("accountVisibility", request.getAccountVisibility());
        }

        return updatedFields;
    }

    public static MypageResponseDTO toResponse(Map<String, Object> updatedFields) {
        return MypageResponseDTO.builder()
                .message("설정이 성공적으로 변경되었습니다.")
                .updatedFields(updatedFields)
                .build();
    }

}
