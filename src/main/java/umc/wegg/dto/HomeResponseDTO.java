package umc.wegg.dto;

import lombok.*;
import umc.wegg.domain.enums.TodoListStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class HomeResponseDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HomeWeekResponseDTO {

        private List<HomeResponseDTO.PlanInfo> weeklyPlans;  // 주간 일정 리스트
        private List<HomeResponseDTO.PostInfo> weeklyPosts;  // 주간 게시물 리스트
        private List<HomeResponseDTO.TodoInfo> todayTodos;   // 오늘 날짜의 투두리스트

        // 투두리스트 통계
        private int totalTodos;                  // 총 투두리스트 항목 수
        private int completedTodos;              // 완료된 항목 수
        private double completionRate;           // 오늘의 투두리스트 달성률

        // 학습 통계
        private int successCount;                // 인증 성공 횟수
        private int totalStudyTime;              // 총 공부 시간 (분 단위)

        private String upcomingPlanAddress; // 📌 추가된 필드 (10분 남은 일정의 주소)

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeMonthResponseDTO {

        private List<HomeResponseDTO.PlanInfo> monthlyPlans;  // 월간 일정 리스트
        private List<HomeResponseDTO.PostInfo> monthlyPosts;  // 월간 게시물 리스트
        private List<HomeResponseDTO.DateSummaryInfo> dateSummaries;  // 날짜별 학습 정보

        // 투두리스트 통계
        private int totalTodos;                  // 총 투두리스트 항목 수
        private int completedTodos;              // 완료된 항목 수
        private double completionRate;           // 이번 달의 투두리스트 달성률

        // 학습 통계
        private int successCount;                // 인증 성공 횟수
        private int totalStudyTime;              // 총 공부 시간 (분 단위)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowResponseDTO {
        private int followerCount;  // 팔로워 수
        private int followingCount; // 팔로잉 수
        private String profileImage; // 프로필 사진
    }


    // Plan 정보 구조
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PlanInfo {
        private Long id;                     // Plan ID
        private LocalDateTime startTime;     // 시작 시간
        private LocalDateTime endTime;       // 종료 시간
    }

    // Post 정보 구조
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PostInfo {
        private Long id;                     // Post ID
        private String imageUrl;             // 게시물 이미지 URL
        private LocalDateTime createdAt;     // 작성 시간
    }

    // 투두리스트 상세 정보 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodoInfo {
        private Long id;                // 투두 ID
        private String content;         // 할 일 내용
        private TodoListStatus status;         // 완료 여부
        private LocalDateTime createdAt; // 작성

        // ✅ Enum 값을 기반으로 isCompleted() 메서드 추가
        public boolean isCompleted() {
            return this.status == TodoListStatus.DONE;
        }
    }



    // 날짜별 시간 및 투두리스트 달성률 정보 구조
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
