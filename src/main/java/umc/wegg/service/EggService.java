package umc.wegg.service;

import umc.wegg.dto.EggRequestDTO;
import umc.wegg.dto.TimeRequestDTO;

import java.util.List;

public interface EggService {
    void recordTime(TimeRequestDTO request);

    List<Object> getCalendarPlans();

    void breakEgg(Long planId, EggRequestDTO request);
}
