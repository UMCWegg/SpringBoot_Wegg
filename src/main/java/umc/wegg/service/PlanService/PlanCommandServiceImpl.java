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
    public Plan planAddMember(PlanRequestDTO.PlanAddDTO request) {
        Plan newPlan = PlanConverter.toPlan(request, userRepository);
        return planRepository.save(newPlan);
    }
}
