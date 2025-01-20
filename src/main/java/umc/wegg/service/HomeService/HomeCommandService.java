package umc.wegg.service.HomeService;

import umc.wegg.dto.HomeResponseDTO;

public interface HomeCommandService {
    HomeResponseDTO getHomeWeekData();
    HomeResponseDTO getHomeMonthData();

    HomeResponseDTO getHomeMonthDataFor(int year, int month);
}
