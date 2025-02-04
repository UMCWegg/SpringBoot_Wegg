package umc.wegg.service.PlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanCommandServiceImpl implements PlanCommandService{
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    public List<Plan> addPlan(PlanRequestDTO.PlanAddDTO request) {
        // planDates에 대해 각각 Plan을 생성하여 반환
        List<Plan> newPlans = PlanConverter.toPlan(request, userRepository);
        // 반환된 계획을 저장
        newPlans.forEach(planRepository::save);

        return newPlans;
    }




    @Override
    public Plan updatePlan(Long planId, PlanRequestDTO.PlanUpdateDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (request.getStatus() != null) {
            existingPlan.setStatus(request.getStatus());
        }

        return planRepository.save(existingPlan);
    }

    @Override
    public Plan onoffPlan(Long planId, PlanRequestDTO.PlanOnoffDTO request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (request.getPlanOn() != null) {
            existingPlan.setPlanOn(request.getPlanOn());
        }

        return planRepository.save(existingPlan);
    }

    @Override
    public void deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new RuntimeException("Plan not found with ID: " + planId);
        }
        planRepository.deleteById(planId);
    }
}
