package umc.wegg.service.PlanService;

import umc.wegg.domain.Plan;
import umc.wegg.dto.PlanRequestDTO;

public interface PlanCommandService {
    Plan planAddMember(PlanRequestDTO.PlanAddDTO request);
}
