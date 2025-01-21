package umc.wegg.converter;

import lombok.RequiredArgsConstructor;
import umc.wegg.domain.Setting;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.AlarmType;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UserConverter {

    public static UserResponseDTO.UserJoinResultDTO toJoinResultDTO(User user){
        return UserResponseDTO.UserJoinResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UserResponseDTO.UserJoinResultDTO toJoinResultDTO(User user, List<UserResponseDTO.UserJoinResultDTO.ExistingUserDTO> existingUsers){
        return UserResponseDTO.UserJoinResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                //연락처에 있는 유저 목록 추가
                .existingUsers(existingUsers)
                .build();
    }

    public static UserResponseDTO.OAuth2UserJoinResultDTO toOAuth2JoinResultDTO(User user, List<UserResponseDTO.OAuth2UserJoinResultDTO.ExistingUserDTO> existingUsers){
        return UserResponseDTO.OAuth2UserJoinResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                //연락처에 있는 유저 목록 추가
                .existingUsers(existingUsers)
                .build();
    }

    public static User toUser(UserRequestDTO.UserJoinDto request){

        User user = User.builder()
                .email(request.getEmail())
                .accountId(request.getAccountId())
                .name(request.getName())
                .job(request.getJob())
                .reason(request.getReason())
                .phone(request.getPhone())
                .build();

        AlarmType alarmType = null;
        if (request.getAlarm()){
            alarmType = AlarmType.BOTH_SINGLE; //알람 허용 시 default: 진동 + 소리 + 단발
        }
        else{
            alarmType = AlarmType.MUTE_SINGLE; //알람 비허용 시 default: 무음 + 단발
        }

        // Setting 객체 생성 및 초기화
        Setting setting = Setting.builder()
                .user(user) // 양방향 관계 설정
                .marketingAgree(request.getMarketingAgree())
                .postAlarm(alarmType)
                .commentAlarm(alarmType)
                .placeAlarm(alarmType)
                .randomAlarm(alarmType)
                .eggAlarm(alarmType)
                .placeCheck(true)
                .randomCheck(true)
                .breakAllow(true)
                .accountVisibility(AccountVisibility.PUBLIC) //default
                .build();

        // 관계 설정
        user.setSetting(setting);

        return user;
    }

    public static User toOAuthUser(UserRequestDTO.OAuth2UserJoinDto request){

        User user = User.builder()
                .accountId(request.getAccountId())
                .name(request.getName())
                .job(request.getJob())
                .reason(request.getReason())
                .phone(request.getPhone())
                .oauthId(request.getOauthId())
                .email(request.getOauthId() + "@wegg.com")
                .password(request.getPassword())
                .build();

        AlarmType alarmType = null;
        if (request.getAlarm()){
            alarmType = AlarmType.BOTH_SINGLE; //알람 허용 시 default: 진동 + 소리 + 단발
        }
        else{
            alarmType = AlarmType.MUTE_SINGLE; //알람 비허용 시 default: 무음 + 단발
        }

        // Setting 객체 생성 및 초기화
        Setting setting = Setting.builder()
                .user(user) // 양방향 관계 설정
                .marketingAgree(request.getMarketingAgree())
                .postAlarm(alarmType)
                .commentAlarm(alarmType)
                .placeAlarm(alarmType)
                .randomAlarm(alarmType)
                .eggAlarm(alarmType)
                .placeCheck(true)
                .randomCheck(true)
                .breakAllow(true)
                .accountVisibility(AccountVisibility.PUBLIC) //default
                .build();

        // 관계 설정
        user.setSetting(setting);

        return user;
    }

    public static UserResponseDTO.LoginResultDTO toLoginResultDTO(boolean success, Long userId) {
        return UserResponseDTO.LoginResultDTO.builder()
                .success(success)
                .userId(userId)
                .build();

    }
}
