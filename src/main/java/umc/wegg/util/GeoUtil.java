package umc.wegg.util;

public class GeoUtil {

    private static final double EARTH_RADIUS = 6371; // 지구 반지름 (킬로미터)

    // 사용자가 계획 범위 내에 있는지 판단하는 메서드
    public static boolean isWithinPlanBoundary(double planLat, double planLon, double userLat, double userLon, double radius) {
        // 위도와 경도 차이로 거리 계산 (킬로미터 단위)
        double latDistance = Math.toRadians(userLat - planLat) * EARTH_RADIUS;
        double lonDistance = Math.toRadians(userLon - planLon) * EARTH_RADIUS * Math.cos(Math.toRadians(planLat));

        // 두 위치 사이의 거리 계산
        double distance = Math.sqrt(latDistance * latDistance + lonDistance * lonDistance);

        // 사용자가 범위 내에 있으면 true
        return distance <= radius;
    }
}
