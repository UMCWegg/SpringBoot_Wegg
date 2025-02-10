package umc.wegg.service.FollowService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.mapping.Follow;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.dto.FollowResponseDTO;
import umc.wegg.repository.ContactFriendsRepository;
import umc.wegg.repository.FollowRepository;
import umc.wegg.repository.UserRepository;

import java.util.List;
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
    public void createFollowRequest(FollowRequestDTO.CreateFollowRequestDTO requestDTO) {
        // 팔로우 요청한 사용자 조회
        User follower = userRepository.findById(requestDTO.getFollowerId())
                .orElseThrow(() -> new IllegalArgumentException("Follower not found with id: " + requestDTO.getFollowerId()));

        // 팔로우 요청받은 사용자 조회
        User followee = userRepository.findById(requestDTO.getFolloweeId())
                .orElseThrow(() -> new IllegalArgumentException("Followee not found with id: " + requestDTO.getFolloweeId()));

        // 팔로우 요청 저장
        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .followStatus(FollowStatus.WAITING) // 대기 상태로 저장
                .build();

        followRepository.save(follow);
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
        return contactFriendsRepository.findByUserIdAndIsFollowingOrderByCreatedAtDesc(myId, FollowStatus.WAITING)
                .stream()
                .map(contact -> FollowResponseDTO.UserRecommendationDTO.builder()
                        .userId(contact.getFriend().getId())
                        .profileImage(contact.getFriend().getProfileImage())
                        .accountId(contact.getFriend().getAccountId())
                        .userName(contact.getFriend().getName())
                        .build())
                .collect(Collectors.toList());
    }

    // 회원님을 위한 사용자 추천 (추후 구현 가능)


}
