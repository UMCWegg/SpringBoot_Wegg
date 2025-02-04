package umc.wegg.service.PlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.Plan;
import umc.wegg.domain.User;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanQueryServiceImpl implements PlanQueryService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        return planRepository.findByUserId(userId);
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @Override
    public PlanResponseDTO.LocationVerificationResponseDTO isUserInPlan(Long planId, Long userId) {
        // 계획 정보 가져오기
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 계획의 위치 (latitude, longitude)
        double planLat = plan.getAddress().getLatitude();
        double planLon = plan.getAddress().getLongitude();

        // 사용자의 위치 (latitude, longitude)
        double userLat = user.getCurrentLat();
        double userLon = user.getCurrentLon();

        // 경계 확인 (500미터 이내인지 확인하는 메서드 호출)
        boolean isWithinBoundary = GeoUtils.isWithinPlanBoundary(planLat, planLon, userLat, userLon, 0.5);  // 0.5km (500m)

        // 결과에 맞는 메시지를 설정
        String message = isWithinBoundary ? "장소 인증에 성공했습니다!" : "장소 인증에 실패했습니다!";

        // LocationVerificationResponseDTO 반환
        return new PlanResponseDTO.LocationVerificationResponseDTO(message);
    }
    public class GeoUtils {

        private static final double EARTH_RADIUS = 6371; // 지구 반지름 (킬로미터)

        // 사용자가 계획 범위 내에 있는지 판단하는 메서드
        public static boolean isWithinPlanBoundary(double planLat, double planLon, double userLat, double userLon, double radius) {
            // 위도와 경도 차이로 거리 계산 (킬로미터 단위)
            double latDistance = Math.toRadians(userLat - planLat) * EARTH_RADIUS;
            double lonDistance = Math.toRadians(userLon - planLon) * EARTH_RADIUS * Math.cos(Math.toRadians(planLat));

            // 두 위치 사이의 거리 계산
            double distance = Math.sqrt(latDistance * latDistance + lonDistance * lonDistance);

            // 사용자가 범위 내에 있으면 true
            return distance <= radius;
        }
    }
}


