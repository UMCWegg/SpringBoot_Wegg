package umc.wegg.service.HomeService;

import umc.wegg.dto.HomeResponseDTO;

public interface HomeCommandService {
    // 본인 주간 데이터 조회
    HomeResponseDTO.HomeWeekResponseDTO getHomeWeekData();

    // 본인 월간 데이터 조회 (세션 기반)
    HomeResponseDTO.HomeMonthResponseDTO getHomeMonthData();

    // 본인 이전달/다음달 이동
    HomeResponseDTO.HomeMonthResponseDTO getHomeMonthDataFor(int year, int month);

    // 본인 팔로우 데이터 조회
    HomeResponseDTO.FollowResponseDTO getHomeFollowData();

    //  친구 월간 데이터 조회 (새로운 메서드)
    HomeResponseDTO.HomeMonthResponseDTO getFriendHomeMonthData(Long userId);

    //  친구 이전달/다음달 이동 (새로운 메서드)
    HomeResponseDTO.HomeMonthResponseDTO getFriendHomeMonthDataFor(Long userId, int year, int month);

    //  친구 팔로우 데이터 조회 (새로운 메서드)
    HomeResponseDTO.FollowResponseDTO getFriendHomeFollowData(Long userId);
}
