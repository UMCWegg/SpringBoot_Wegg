package umc.wegg.converter;

import lombok.RequiredArgsConstructor;
import umc.wegg.domain.ContactFriend;
import umc.wegg.domain.Setting;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.AlarmType;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserConverter {

    public static UserResponseDTO.UserJoinResultDTO toJoinResultDTO(User user){
        return UserResponseDTO.UserJoinResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UserResponseDTO.UserJoinResultDTO toJoinResultDTO(User user, List<UserResponseDTO.UserJoinResultDTO.ContactFriendDto> contactFriends){
        return UserResponseDTO.UserJoinResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                //연락처에 있는 유저 목록 추가
                .contactFriends(contactFriends)
                .build();
    }

    public static UserResponseDTO.OAuth2UserJoinResultDTO toOAuth2JoinResultDTO(User user, List<UserResponseDTO.OAuth2UserJoinResultDTO.ContactFriendDto> contactFriends){
        return UserResponseDTO.OAuth2UserJoinResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                //연락처에 있는 유저 목록 추가
                .contactFriends(contactFriends)
                .build();
    }

    public static User toUser(UserRequestDTO.UserJoinDto request, List<UserResponseDTO.ContactFriendDTO> contactFriends){

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
                .postAlarm(true)
                .commentAlarm(true)
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

        // ContactFriends 초기화
        if (contactFriends != null && !contactFriends.isEmpty()) {
            List<ContactFriend> contactFriendList = contactFriends.stream()
                    .map(contactFriend -> ContactFriend.builder()
                            .user(user)
                            .friend(contactFriend.getFriend())
                            .phoneNum(contactFriend.getPhone()) // 기존 사용자의 전화번호 추가
                            .isFollowing(false)
                            .build())
                    .collect(Collectors.toList());

            user.setContactFriendList(contactFriendList);
        }

        return user;
    }

    public static User toOAuthUser(UserRequestDTO.OAuth2UserJoinDto request, List<UserResponseDTO.ContactFriendDTO> contactFriends){

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
                .postAlarm(true)
                .commentAlarm(true)
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

        // ContactFriends 초기화
        if (contactFriends != null && !contactFriends.isEmpty()) {
            List<ContactFriend> contactFriendList = contactFriends.stream()
                    .map(contactFriend -> ContactFriend.builder()
                            .user(user)
                            .friend(contactFriend.getFriend())
                            .phoneNum(contactFriend.getPhone()) // 기존 사용자의 전화번호 추가
                            .isFollowing(false)
                            .build())
                    .collect(Collectors.toList());

            user.setContactFriendList(contactFriendList);
        }

        return user;
    }

    public static UserResponseDTO.LoginResultDTO toLoginResultDTO(boolean success, Long userId) {
        return UserResponseDTO.LoginResultDTO.builder()
                .success(success)
                .userId(userId)
                .build();

    }

    public static List<ContactFriend> toContactFriendEntities(User user, List<UserResponseDTO.ContactFriendDTO> contactFriends) {
        return contactFriends.stream()
                .map(contactFriend -> ContactFriend.builder()
                        .user(user)
                        .friend(contactFriend.getFriend())
                        .phoneNum(contactFriend.getPhone()) // 기존 사용자의 전화번호 추가
                        .isFollowing(false)
                        .build())
                .collect(Collectors.toList());
    }
}
