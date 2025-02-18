package umc.wegg.service.FollowService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.mapping.Follow;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.dto.FollowResponseDTO;
import umc.wegg.repository.ContactFriendsRepository;
import umc.wegg.repository.FollowRepository;
import umc.wegg.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowCommandServiceImpl implements FollowCommandService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ContactFriendsRepository contactFriendsRepository; // 연락처 친구 조회를 위해 필요

    /**
     * 팔로우 요청 생성
     * @param requestDTO 팔로우 요청 데이터
     */
    @Override
    public FollowStatus createFollowRequest(FollowRequestDTO.CreateFollowRequestDTO requestDTO) {
        // 팔로우 요청한 사용자 조회
        User follower = userRepository.findById(requestDTO.getFollowerId())
                .orElseThrow(() -> new IllegalArgumentException("Follower not found with id: " + requestDTO.getFollowerId()));

        // 팔로우 요청받은 사용자 조회
        User followee = userRepository.findById(requestDTO.getFolloweeId())
                .orElseThrow(() -> new IllegalArgumentException("Followee not found with id: " + requestDTO.getFolloweeId()));

        // 이미 팔로우 중인지 확인
        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new IllegalStateException("이미 팔로우 중입니다.");
        }

        // 계정 공개 상태 확인
        AccountVisibility visibility = followee.getSetting().getAccountVisibility();
        FollowStatus followStatus = (visibility == AccountVisibility.PUBLIC) ? FollowStatus.SUCCEEDED : FollowStatus.WAITING;

        // 팔로우 요청 저장
        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .followStatus(followStatus)
                .build();

        followRepository.save(follow);

        return followStatus; // 컨트롤러에서 응답 메시지를 다르게 설정할 수 있도록 반환
    }



    /**
     * 팔로우 요청 상태 결정 (수락 또는 거절)
     * @param requestDTO 팔로우 요청 처리 데이터
     * @param followStatus 수락 또는 거절 상태
     */
    @Override
    public void decideFollowRequest(FollowRequestDTO.DecideFollowRequestDTO requestDTO, FollowStatus followStatus) {
        // 팔로우 요청 조회
        Follow follow = followRepository.findById(requestDTO.getFollowId())
                .orElseThrow(() -> new IllegalArgumentException("Follow request not found with id: " + requestDTO.getFollowId()));

        // 팔로우 상태 업데이트
        follow.setFollowStatus(followStatus);
        followRepository.save(follow);
    }

    // 나에게 온 팔로우 요청들
    @Override
    public List<FollowResponseDTO.UserRecommendationDTO> getFollowRequests(Long myId) {
        // 1️. 사용자 조회
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + myId));

        // 2️. 계정 공개 여부 확인
        if (user.getSetting() == null || user.getSetting().getAccountVisibility() == AccountVisibility.PUBLIC) {
            // 공개 계정이면 빈 리스트 반환
            return Collections.emptyList();
        }

        // 3️. 비공개 계정이면 팔로우 요청 리스트 반환
        return followRepository.findByFolloweeIdAndFollowStatusOrderByCreatedAtDesc(myId, FollowStatus.WAITING)
                .stream()
                .map(follow -> FollowResponseDTO.UserRecommendationDTO.builder()
                        .userId(follow.getFollower().getId())
                        .profileImage(follow.getFollower().getProfileImage())
                        .accountId(follow.getFollower().getAccountId())
                        .userName(follow.getFollower().getName())
                        .build())
                .collect(Collectors.toList());
    }


    // 연락처에 있는 사용자 추천
    @Override
    public List<FollowResponseDTO.UserRecommendationDTO> getContactRecommendations(Long myId) {
        return contactFriendsRepository.findByUserIdAndIsFollowingYet(myId, FollowStatus.YET)
                .stream()
                .map(contact -> FollowResponseDTO.UserRecommendationDTO.builder()
                        .userId(contact.getFriend().getId())
                        .profileImage(contact.getFriend().getProfileImage())
                        .accountId(contact.getFriend().getAccountId())
                        .userName(contact.getFriend().getName())
                        .build())
                .collect(Collectors.toList());
    }



    // 회원님을 위한 사용자 추천 -> 팔로워 기반
//    @Override
//    public List<FollowResponseDTO.UserRecommendationDTO> getGeneralRecommendations(Long myId) {
//        List<Object[]> recommendedUsersData = followRepository.findRecommendedUsersWithFollowCount(myId, PageRequest.of(0, 10));
//
//        // 1. 팔로워들의 팔로잉 중, 같이 팔로우한 사람이 많은 순으로 추천
//        List<FollowResponseDTO.UserRecommendationDTO> recommendations = recommendedUsersData.stream()
//                .map(data -> {
//                    User user = (User) data[0];
//                    Long followCount = (Long) data[1];
//                    return FollowResponseDTO.UserRecommendationDTO.builder()
//                            .userId(user.getId())
//                            .profileImage(user.getProfileImage())
//                            .accountId(user.getAccountId())
//                            .userName(user.getName())
//                            //.commonFollowerCount(followCount) // 몇 명이 같이 팔로우하고 있는지 표시
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // 2. 만약 추천할 사람이 없으면, 인기 유저 추천
//        if (recommendations.isEmpty()) {
//            List<User> popularUsers = followRepository.findTopFollowedUsers(PageRequest.of(0, 10));
//            recommendations = popularUsers.stream()
//                    .map(user -> FollowResponseDTO.UserRecommendationDTO.builder()
//                            .userId(user.getId())
//                            .profileImage(user.getProfileImage())
//                            .accountId(user.getAccountId())
//                            .userName(user.getName())
//                            //.commonFollowerCount(0L) // 기본값 0
//                            .build())
//                    .collect(Collectors.toList());
//        }
//
//        return recommendations;
//    }
//    @Override
//    public List<FollowResponseDTO.UserRecommendationDTO> getGeneralRecommendations(Long myId) {
//        List<Object[]> recommendedUsersData = followRepository.findRecommendedUsersWithFollowCount(myId, PageRequest.of(0, 10));
//
//        // 1️⃣ 팔로워들의 팔로잉 중, 같이 팔로우한 사람이 많은 순으로 추천
//        List<FollowResponseDTO.UserRecommendationDTO> recommendations = recommendedUsersData.stream()
//                .map(data -> {
//                    User user = (User) data[0];
//                    Long followCount = (Long) data[1];
//                    return FollowResponseDTO.UserRecommendationDTO.builder()
//                            .userId(user.getId())
//                            .profileImage(user.getProfileImage())
//                            .accountId(user.getAccountId())
//                            .userName(user.getName())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // 2️⃣ 팔로워 기반 추천이 10명보다 적으면, 인기 유저 추가
//        if (recommendations.size() < 10) {
//            int remainingSlots = 10 - recommendations.size(); // 부족한 추천 개수 계산
//            List<User> popularUsers = followRepository.findTopFollowedUsers(PageRequest.of(0, remainingSlots));
//
//            List<FollowResponseDTO.UserRecommendationDTO> popularRecommendations = popularUsers.stream()
//                    .map(user -> FollowResponseDTO.UserRecommendationDTO.builder()
//                            .userId(user.getId())
//                            .profileImage(user.getProfileImage())
//                            .accountId(user.getAccountId())
//                            .userName(user.getName())
//                            .build())
//                    .collect(Collectors.toList());
//
//            recommendations.addAll(popularRecommendations); // 인기 유저 추가
//        }
//
//        return recommendations;
//    }

//    @Override
//    public List<FollowResponseDTO.UserRecommendationDTO> getGeneralRecommendations(Long myId) {
//        List<Object[]> recommendedUsersData = followRepository.findRecommendedUsersWithFollowCount(myId, PageRequest.of(0, 10));
//
//        // 1️⃣ 팔로워 기반 추천
//        List<FollowResponseDTO.UserRecommendationDTO> recommendations = recommendedUsersData.stream()
//                .map(data -> {
//                    User user = (User) data[0];
//                    Long followCount = (Long) data[1];
//                    return FollowResponseDTO.UserRecommendationDTO.builder()
//                            .userId(user.getId())
//                            .profileImage(user.getProfileImage())
//                            .accountId(user.getAccountId())
//                            .userName(user.getName())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // 2️⃣ 팔로워 기반 추천이 10명보다 적으면, 인기 유저 추가
//        if (recommendations.size() < 10) {
//            int remainingSlots = 10 - recommendations.size();
//            List<User> popularUsers = followRepository.findTopFollowedUsers(PageRequest.of(0, remainingSlots));
//
//            // 3️⃣ 인기 유저 추천도 비어있으면, 랜덤 유저 추천 추가
//            if (popularUsers.isEmpty()) {
//                popularUsers = followRepository.findRandomUsers(PageRequest.of(0, remainingSlots));
//            }
//
//            List<FollowResponseDTO.UserRecommendationDTO> popularRecommendations = popularUsers.stream()
//                    .map(user -> FollowResponseDTO.UserRecommendationDTO.builder()
//                            .userId(user.getId())
//                            .profileImage(user.getProfileImage())
//                            .accountId(user.getAccountId())
//                            .userName(user.getName())
//                            .build())
//                    .toList();
//
//            recommendations.addAll(popularRecommendations); // 인기/랜덤 유저 추가
//        }
//
//        return recommendations;
//    }

    @Override
    public List<FollowResponseDTO.UserRecommendationDTO> getGeneralRecommendations(Long myId) {
        // 추천 유저 데이터를 가져옴 (최대 10명), 팔로워 기반 추천을 우선
        List<Object[]> recommendedUsersData = followRepository.findRecommendedUsersWithFollowCount(myId, PageRequest.of(0, 10));
        Set<Long> uniqueUserIds = new HashSet<>(); // 중복 방지를 위한 Set

        // 1️. 팔로워 기반 추천 (가장 먼저 추천할 유저 리스트 생성)
        List<FollowResponseDTO.UserRecommendationDTO> recommendations = recommendedUsersData.stream()
                .map(data -> {
                    User user = (User) data[0]; // 추천된 유저 정보
                    return FollowResponseDTO.UserRecommendationDTO.builder()
                            .userId(user.getId())
                            .profileImage(user.getProfileImage())
                            .accountId(user.getAccountId())
                            .userName(user.getName())
                            .build();
                })
                .filter(dto -> dto.getUserId() != myId) // 내 계정 제외
                .filter(dto -> uniqueUserIds.add(dto.getUserId())) // Set을 이용하여 중복 제거 (이미 추가된 유저는 걸러짐)
                .collect(Collectors.toList());

        // 2️. 추천 인원이 10명보다 부족할 경우, 추가 유저를 채움
        while (recommendations.size() < 10) {
            int remainingSlots = 10 - recommendations.size(); // 부족한 추천 인원 계산

            // 2-1. 인기 유저 추천 (팔로워 수가 많은 유저 우선 추가)
            List<User> additionalUsers = followRepository.findTopFollowedUsers(PageRequest.of(0, remainingSlots));

            // 2-2. 인기 유저가 부족할 경우, 랜덤 유저 추천을 추가
            if (additionalUsers.size() < remainingSlots) { // 추가 유저가 부족한 경우
                Long totalUsers = userRepository.countTotalUsers(); // 전체 유저 수 조회
                if (totalUsers > 0) {
                    additionalUsers = userRepository.findRandomUsers(PageRequest.of(0, remainingSlots)); // 랜덤 유저 추가
                }
            }

            // 2-3. 추가된 유저 리스트를 DTO 형태로 변환 및 중복 제거
            List<FollowResponseDTO.UserRecommendationDTO> additionalRecommendations = additionalUsers.stream()
                    .map(user -> FollowResponseDTO.UserRecommendationDTO.builder()
                            .userId(user.getId())
                            .profileImage(user.getProfileImage())
                            .accountId(user.getAccountId())
                            .userName(user.getName())
                            .build())
                    .filter(dto -> !Objects.equals(dto.getUserId(), myId)) // 내 계정 제외
                    .filter(dto -> uniqueUserIds.add(dto.getUserId())) // 중복 제거 (이미 추천된 유저는 제외)
                    .toList();

            recommendations.addAll(additionalRecommendations); // 추가된 유저를 추천 리스트에 포함

            // 2-4. 추가할 유저가 더 이상 없으면 루프 종료 (무한 루프 방지)
            if (additionalRecommendations.isEmpty()) {
                break;
            }
        }

        // 3. 최종 추천 리스트 반환 (최대 10명, 부족할 경우 가능한 범위 내에서 반환)
        return recommendations;
    }

}
