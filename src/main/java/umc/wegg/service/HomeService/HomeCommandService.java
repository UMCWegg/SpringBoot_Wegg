package umc.wegg.service.HomeService;

import umc.wegg.dto.HomeResponseDTO;

public interface HomeCommandService {
    HomeResponseDTO.HomeWeekResponseDTO getHomeWeekData();
    HomeResponseDTO.HomeMonthResponseDTO getHomeMonthData();
    HomeResponseDTO.FollowResponseDTO getHomeFollowData();
    HomeResponseDTO.HomeMonthResponseDTO getHomeMonthDataFor(int year, int month);
}
