package umc.wegg.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.EggConverter;
import umc.wegg.domain.Egg;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Time;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.EggStatus;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.dto.EggRequestDTO;
import umc.wegg.dto.TimeRequestDTO;
import umc.wegg.repository.EggRepository;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.TimeRepository;
import umc.wegg.repository.UserRepository;

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

        // 플랜 상태가 SUCCEEDED인지 확인
        if (plan.getStatus() != PlanStatus.SUCCEEDED) {
            throw new IllegalStateException("계획이 완료된 상태에서만 알을 깰 수 있습니다.");
        }

        // 알 상태 변경 및 사용자 정보 추가
        egg.setStatus(EggStatus.BREAK);
        egg.setBrokenTime(LocalDateTime.now());
        egg.setUser(userRepository.findById(request.getBreakerId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")));

        eggRepository.save(egg);
    }
}
