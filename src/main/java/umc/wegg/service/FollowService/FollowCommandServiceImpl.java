package umc.wegg.service.FollowService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.mapping.Follow;
import umc.wegg.dto.FollowRequestDTO;
import umc.wegg.repository.FollowRepository;
import umc.wegg.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FollowCommandServiceImpl implements FollowCommandService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

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

}
