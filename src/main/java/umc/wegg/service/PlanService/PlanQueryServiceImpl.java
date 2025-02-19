package umc.wegg.service.PlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.Egg;
import umc.wegg.domain.Plan;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.EggStatus;
import umc.wegg.domain.enums.NotificationType;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.NotificationService.NotificationService;
import umc.wegg.util.GeoUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanQueryServiceImpl implements PlanQueryService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        return planRepository.findByUserId(userId);
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }


    @Override
    public PlanResponseDTO.LocationVerificationResponseDTO isUserInPlan(Long planId, Long userId, double userLat, double userLon, int type) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        double planLat = plan.getAddress().getLatitude();
        double planLon = plan.getAddress().getLongitude();

        boolean isWithinBoundary = GeoUtil.isWithinPlanBoundary(planLat, planLon, userLat, userLon, 0.5);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = (type == 1) ? plan.getStartTime() : plan.getRandomTime();
        LocalDateTime endTime = (type == 1) ? startTime.plusMinutes(plan.getLateTime().getMinutes()) : startTime.plusMinutes(2);

        String message;
        if (isWithinBoundary && now.isAfter(startTime.minusMinutes(5)) && now.isBefore(endTime)) {
            message = "장소 인증에 성공했습니다!";
            plan.setStatus(PlanStatus.STARTED);
        } else {
            message = "장소 인증에 실패했습니다!";
            failPlan(plan);
        }

        planRepository.save(plan);

        return new PlanResponseDTO.LocationVerificationResponseDTO(message);
    }

    public void failPlan(Plan plan) {
        plan.setStatus(PlanStatus.FAILED);
        Egg egg = plan.getEgg();
        if (egg != null) {
            egg.setStatus(EggStatus.BREAK);
            egg.setBrokenTime(LocalDateTime.now());
        }
    }

    public void schedulePlanVerification(Plan plan) {
        LocalDateTime checkTime = plan.getStartTime().plusMinutes(plan.getLateTime().getMinutes());

        notificationService.scheduleTask(() -> {
            Plan latestPlan = planRepository.findById(plan.getId()).orElse(null);
            if (latestPlan != null && latestPlan.getStatus() != PlanStatus.STARTED) {
                failPlan(latestPlan);
                planRepository.save(latestPlan);

                // 계획이 실제로 실패한 경우에만 알림 전송
                notificationService.scheduleNotification(latestPlan.getUser(),
                        NotificationType.PLACE_VERIFY,
                        LocalDateTime.now(), // 지금 즉시 알림 전송
                        "장소 인증을 하지 않아 계획이 실패 처리되었습니다.",
                        "/plans/" + latestPlan.getId(), null);
            }
        }, checkTime);
    }
}