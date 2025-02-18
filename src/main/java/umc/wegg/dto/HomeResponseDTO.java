package umc.wegg.dto;

import lombok.*;
import umc.wegg.domain.enums.EggStatus;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.enums.PlanStatus;
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

        private List<DailyData> weeklyData;                 // 날짜별 통합 리스트
        private List<HomeResponseDTO.TodoInfo> todayTodos;   // 오늘 날짜의 투두리스트

        // 투두리스트 통계
        private int totalTodos;                  // 총 투두리스트 항목 수
        private int completedTodos;              // 완료된 항목 수
        private double completionRate;           // 오늘의 투두리스트 달성률

        // 학습 통계
        private int successCount;                // 인증 성공 횟수
        private int totalStudyTime;              // 총 공부 시간 (분 단위)

        private String upcomingPlanAddress; //  추가된 필드 (10분 남은 일정의 주소)
        private int availablePoints; //  받을 수 있는 포인트 (3의 배수일 때만)
        private boolean canReceivePoints; //  포인트 받을 수 있는지 여부

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeMonthResponseDTO {

        private List<DailyData> monthlyData;          // 날짜별 통합 리스트
        private List<DateSummaryInfo> dateSummaries;  // 날짜별 학습 정보
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowResponseDTO {
        private int followerCount;  // 팔로워 수
        private int followingCount; // 팔로잉 수
        private String profileImage; // 프로필 사진
        private String accountId;   // 사용자 계정 ID
        private FollowStatus followStatus; // ✅ 친구 홈에서만 추가될 필드

        // 본인 홈에서 사용하는 생성자 (팔로우 상태 없음)
        public FollowResponseDTO(int followerCount, int followingCount, String profileImage, String accountId) {
            this.followerCount = followerCount;
            this.followingCount = followingCount;
            this.profileImage = profileImage;
            this.accountId = accountId;
            this.followStatus = null; // 본인 홈에서는 팔로우 상태 없음
        }
}

    // plan 데이터와 post 데이터를 합침
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyData {
        private LocalDate date;  // 해당 날짜
        private PlanInfo plan;   // 일정 (존재하면 포함)
        private PostInfo post;   // 게시물 (존재하면 포함)
    }


    // Plan 정보 구조
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PlanInfo {
        private Long id;                     // Plan ID
        private LocalDateTime startTime;     // 시작 시간
        private LocalDateTime endTime;       // 종료 시간
        private PlanStatus status;           // 계획 상태(YET, SUCCEEDED, FAILED)
        private EggStatus eggStatus;         // 해당 계획과 연결된 Egg 상태
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
        private Long todoId;                 // 투두 ID
        private String content;          // 할 일 내용
        private TodoListStatus status;   // 완료 여부
        private LocalDateTime createdAt; // 작성

        //  Enum 값을 기반으로 isCompleted() 메서드 추가
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
//        private int totalTodos;           // 총 투두리스트 항목 수
//        private int completedTodos;       // 완료된 항목 수
        private double completionRate;    // 투두리스트 달성률
        private boolean hasFailedPlan;    // 실패한 계획이 있는지 여부
    }

}
