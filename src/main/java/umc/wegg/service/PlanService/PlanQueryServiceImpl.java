package umc.wegg.service.PlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.Plan;
import umc.wegg.repository.PlanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanQueryServiceImpl implements PlanQueryService {
    private final PlanRepository planRepository;

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        return planRepository.findByUserId(userId);
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }
}

