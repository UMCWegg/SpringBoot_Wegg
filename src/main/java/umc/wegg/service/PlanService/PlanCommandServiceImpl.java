package umc.wegg.service.PlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.PlanConverter;
import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanRequestDTO;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PlanCommandServiceImpl implements PlanCommandService{
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    public Plan addPlan(PlanRequestDTO.PlanAddDTO request) {
        Plan newPlan = PlanConverter.toPlan(request, userRepository);
        return planRepository.save(newPlan);
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
    public void deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new RuntimeException("Plan not found with ID: " + planId);
        }
        planRepository.deleteById(planId);
    }
}
