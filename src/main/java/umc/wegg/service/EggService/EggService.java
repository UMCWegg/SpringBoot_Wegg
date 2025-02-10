package umc.wegg.service.EggService;

import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.dto.TimeRequestDTO;

import java.util.List;

public interface EggService {
    void recordTime(AuthenticatedUser authenticatedUser, TimeRequestDTO request);

    List<Object> getCalendarPlans();

    void breakEgg(AuthenticatedUser authenticatedUser, Long planId);
}
