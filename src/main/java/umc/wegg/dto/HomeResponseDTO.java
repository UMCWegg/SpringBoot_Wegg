package umc.wegg.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HomeResponseDTO {

    private List<PlanInfo> plans;        // 일정 정보 리스트
    private List<PostInfo> posts;        // 게시물 정보 리스트
    private List<DateSummaryInfo> dateSummaries = List.of(); // 날짜별 통합 정보(공부시간, 투두리스트 달성률) 리스트

    // 투두리스트 통계
    private int totalTodos;                  // 총 투두리스트 항목 수
    private int completedTodos;              // 완료된 항목 수
    private double completionRate;           // 오늘의 투두리스트 달성률

    // 기타 통계
    private int successCount;                // 인증 성공 횟수
    private int totalStudyTime;              // 총 공부 시간

    // Plan 정보 구조
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PlanInfo {
        private Long id;                     // Plan ID
        private LocalDateTime startTime;     // 시작 시간
        private LocalDateTime endTime;       // 종료 시간
    }

    // Post 정보 구조
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PostInfo {
        private Long id;                     // Post ID
        private String imageUrl;             // 게시물 이미지 URL
        private LocalDateTime createdAt;     // 작성 시간
    }

    // 날짜별 시간 및 투두리스트 달성률 정보 구조
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class DateSummaryInfo {
        private LocalDate date;           // 날짜
        private int studyTime;            // 공부 시간 (분 단위)
        private int totalTodos;           // 총 투두리스트 항목 수
        private int completedTodos;       // 완료된 항목 수
        private double completionRate;    // 투두리스트 달성률
    }

}
