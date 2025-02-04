package umc.wegg.service.EggService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.EggConverter;
import umc.wegg.domain.Egg;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Time;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.EggStatus;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.dto.EggRequestDTO;
import umc.wegg.dto.TimeRequestDTO;
import umc.wegg.repository.*;
import umc.wegg.service.NotificationService.NotificationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EggServiceImpl implements EggService {

    private final EggRepository eggRepository;
    private final PlanRepository planRepository;
    private final TimeRepository timeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private final FollowRepository followRepository;

    @Override
    @Transactional
    public void recordTime(TimeRequestDTO request) {
        // 사용자 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // Time 객체 생성
        Time time = EggConverter.toTime(request, user);
        timeRepository.save(time); // Time 저장
    }

    @Override
    public List<Object> getCalendarPlans() {
        return Collections.singletonList(planRepository.findAll());
    }

    @Override
    @Transactional
    public void breakEgg(Long planId, EggRequestDTO request) {
        // 계획 및 알 확인
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 플랜이 존재하지 않습니다."));

        Egg egg = plan.getEgg();
        if (egg == null) {
            throw new IllegalStateException("해당 플랜에 연결된 알이 존재하지 않습니다.");
        }

        // 이미 깨진 알인지 검증
        if (egg.getStatus() == EggStatus.BREAK) {
            throw new IllegalStateException("이미 깨진 알입니다.");
        }

        // 플랜 상태가 YET인지 확인
        if (plan.getStatus() != PlanStatus.YET) {
            throw new IllegalStateException("계획이 완료되지 않은 상태에서만 알을 깰 수 있습니다.");
        }

        // 알을 깨려는 사용자 확인
        User breaker = userRepository.findById(request.getBreakerId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 계획을 만든 사용자 (알을 가진 사용자)
        User eggOwner = plan.getUser();

        // 알을 깨려는 사람이 계획을 만든 사람과 팔로우 관계인지 확인
        boolean isFollower = followRepository.existsByFollowerAndFolloweeAndFollowStatus(
                breaker, eggOwner, FollowStatus.SUCCEEDED
        );

        if (!isFollower) {
            throw new IllegalStateException("알을 깨려면 해당 사용자와 팔로우 관계여야 합니다.");
        }

        // 알 상태 변경 및 사용자 정보 추가
        egg.setStatus(EggStatus.BREAK);
        egg.setBrokenTime(LocalDateTime.now());
        egg.setUser(userRepository.findById(request.getBreakerId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")));

        eggRepository.save(egg);

        User breakUser = egg.getUser();
        // 알림 메시지 작성
        String message = breakUser.getAccountId() + "님이 알을 깨고 달아났습니다!";

        // 알림 전송
        notificationService.sendNotificationToEggOwner(plan.getUser(), message, "EGG");
    }
}
