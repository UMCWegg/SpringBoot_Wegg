package umc.wegg.service.HomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.HomeConverter;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Post;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeCommandServiceImpl implements HomeCommandService {

    private final PlanRepository planRepository;
    private final PostRepository postRepository;
    private final TodoRepository todoRepository;
    private final TimeRepository timeRepository;
    private final UserRepository userRepository;
    private final HomeConverter homeConverter;
    private final FollowRepository followRepository;

    @Override
    public HomeResponseDTO.HomeWeekResponseDTO getHomeWeekData(AuthenticatedUser authenticatedUser) {
        Long userId = 1L;//authenticatedUser.getUserId(); // 로그인된 사용자 ID

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY);

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.DailyData> weeklyData = new ArrayList<>();

        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // 일정 및 게시물 조회
            Plan plan = allPlans.stream()
                    .filter(p -> p.getStartTime().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Plan::getStartTime)) // 📌 가장 빠른 일정 선택
                    .orElse(null);

            Post post = allPosts.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Post::getCreatedAt)) // 📌 첫 게시물 선택
                    .orElse(null);

            HomeResponseDTO.PlanInfo planInfo = (plan != null)
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0)
                    : null;

            HomeResponseDTO.PostInfo postInfo = (post != null)
                    ? homeConverter.convertPostsToPostInfos(List.of(post)).get(0)
                    : null;

            //  과거 데이터 처리 (PlanStatus == FAILED 인 경우 표시)
            if (currentDate.isBefore(today)) {
                if (plan != null && plan.getStatus() == PlanStatus.FAILED) {
                    weeklyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, null)); // 실패한 계획 표시
                } else {
                    weeklyData.add(new HomeResponseDTO.DailyData(currentDate, null, postInfo)); // 게시물만 표시
                }
            }
            //  오늘 데이터 처리 (현재 시간 기준)
            else if (currentDate.equals(today)) {
                if (plan != null) {
                    if (plan.getStartTime().isBefore(now)) {
                        // 현재 시간 이전이면 과거로 간주
                        if (postInfo != null) {
                            planInfo = null; // 게시물이 있으면 계획 대신 표시
                        }
                    }
                }
                weeklyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, postInfo));
            }
            //  미래 데이터 처리
            else {
                weeklyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, null));
            }
        }

        //  투두리스트 및 통계 데이터 설정
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        List<HomeResponseDTO.TodoInfo> todayTodos = homeConverter.convertTodosToTodoInfos(todos);

        int totalTodos = todayTodos.size();
        int completedTodos = (int) todayTodos.stream().filter(HomeResponseDTO.TodoInfo::isCompleted).count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        int successCount = Optional.ofNullable(userRepository.findSuccessCountByUserId(userId)).orElse(0);
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream().mapToInt(time -> time.getDuration()).sum();

        //  가장 가까운 일정 찾기
        Plan closestPlan = allPlans.stream()
                .filter(plan -> plan.getStartTime().isAfter(now))
                .min(Comparator.comparing(Plan::getStartTime))
                .orElse(null);

        String upcomingPlanAddress = null;
        if (closestPlan != null) {
            long minutesUntilStart = java.time.Duration.between(now, closestPlan.getStartTime()).toMinutes();
            if (minutesUntilStart <= 10) {
                upcomingPlanAddress = closestPlan.getAddress().getAddress(); // ✅ Address 객체에서 가져오기
            }
        }

        //  포인트 지급 로직 추가
        int availablePoints = 0;
        boolean canReceivePoints = false;
        Integer lastReceivedSuccessCount = userRepository.findLastReceivedSuccessCount(userId).orElse(0);
        for (int i = lastReceivedSuccessCount + 3; i <= successCount; i += 3) {
            availablePoints += 3;
        }
        canReceivePoints = availablePoints > 0;

        //  빌더 패턴 사용하여 DTO 생성
        return HomeResponseDTO.HomeWeekResponseDTO.builder()
                .weeklyData(weeklyData)
                .todayTodos(todayTodos)
                .totalTodos(totalTodos)
                .completedTodos(completedTodos)
                .completionRate(completionRate)
                .successCount(successCount)
                .totalStudyTime(totalStudyTime)
                .upcomingPlanAddress(upcomingPlanAddress)
                .availablePoints(availablePoints)
                .canReceivePoints(canReceivePoints)
                .build();
    }



    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getHomeMonthData(AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.DailyData> monthlyData = new ArrayList<>();
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = new ArrayList<>();

        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // 일정 및 게시물 조회
            Plan plan = allPlans.stream()
                    .filter(p -> p.getStartTime().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Plan::getStartTime)) // 📌 가장 빠른 일정 선택
                    .orElse(null);

            Post post = allPosts.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Post::getCreatedAt)) // 📌 첫 게시물 선택
                    .orElse(null);

            HomeResponseDTO.PlanInfo planInfo = (plan != null)
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0)
                    : null;

            HomeResponseDTO.PostInfo postInfo = (post != null)
                    ? homeConverter.convertPostsToPostInfos(List.of(post)).get(0)
                    : null;

            // 과거 데이터 처리 (PlanStatus == FAILED 인 경우 표시)
            if (currentDate.isBefore(today)) {
                if (plan != null && plan.getStatus() == PlanStatus.FAILED) {
                    monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, null)); // 실패한 계획 표시
                } else {
                    monthlyData.add(new HomeResponseDTO.DailyData(currentDate, null, postInfo)); // 게시물만 표시
                }
            }
            // 오늘 데이터 처리 (현재 시간 기준)
            else if (currentDate.equals(today)) {
                if (plan != null) {
                    if (plan.getStartTime().isBefore(now)) {
                        // 현재 시간 이전이면 과거로 간주
                        if (postInfo != null) {
                            planInfo = null; // 게시물이 있으면 계획 대신 표시
                        }
                    }
                }
                monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, postInfo));
            }
            // 미래 데이터 처리
            else {
                monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, null));
            }
            //  게시물이 존재하는 경우에만 공부 시간 및 투두리스트 정보 추가
            if (post != null) {
                List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, currentDate);
                int totalTodos = todos.size();
                int completedTodos = (int) todos.stream()
                        .filter(todo -> todo.getStatus() == TodoListStatus.DONE)
                        .count();
                double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

                int studyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, currentDate)
                        .stream()
                        .mapToInt(time -> time.getDuration())
                        .sum();

                dateSummaries.add(new HomeResponseDTO.DateSummaryInfo(
                        currentDate, studyTime, totalTodos, completedTodos, completionRate
                ));
            }

        }

// 날짜별 공부 시간 및 투두리스트 달성률 계산(게시물이 있던 없던 모든 시간 데이터가 뜨는 코드)
//        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = homeConverter.calculateDateSummaries(
//                userId, monthStart, monthEnd, timeRepository, todoRepository
//        );

        return new HomeResponseDTO.HomeMonthResponseDTO(
                monthlyData,
                dateSummaries
        );
    }


    @Override
    public HomeResponseDTO.FollowResponseDTO getHomeFollowData(AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 팔로워/팔로잉 수 계산
        int followerCount = followRepository.countFollowers(userId);
        int followingCount = followRepository.countFollowing(userId);

        String profileImage = user.getProfileImage();

        return new HomeResponseDTO.FollowResponseDTO(
                followerCount,
                followingCount,
                profileImage,
                user.getAccountId()
        );
    }

    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getHomeMonthDataFor(AuthenticatedUser authenticatedUser, int year, int month) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate monthStart = LocalDate.of(year, month, 1); // 해당 달 첫째 날
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth()); // 해당 달 마지막 날

        // 해당 월의 일정 및 게시물 조회
        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.DailyData> monthlyData = new ArrayList<>();
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = new ArrayList<>();

        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // 일정 및 게시물 조회
            Plan plan = allPlans.stream()
                    .filter(p -> p.getStartTime().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Plan::getStartTime)) // 📌 가장 빠른 일정 선택
                    .orElse(null);

            Post post = allPosts.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Post::getCreatedAt)) // 📌 첫 게시물 선택
                    .orElse(null);

            HomeResponseDTO.PlanInfo planInfo = (plan != null)
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0)
                    : null;

            HomeResponseDTO.PostInfo postInfo = (post != null)
                    ? homeConverter.convertPostsToPostInfos(List.of(post)).get(0)
                    : null;

            // 과거 데이터 처리 (PlanStatus == FAILED 인 경우 표시)
            if (currentDate.isBefore(today)) {
                if (plan != null && plan.getStatus() == PlanStatus.FAILED) {
                    monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, null)); // 실패한 계획 표시
                } else {
                    monthlyData.add(new HomeResponseDTO.DailyData(currentDate, null, postInfo)); // 게시물만 표시
                }
            }
            // 오늘 데이터 처리 (현재 시간 기준)
            else if (currentDate.equals(today)) {
                if (plan != null) {
                    if (plan.getStartTime().isBefore(now)) {
                        // 현재 시간 이전이면 과거로 간주
                        if (postInfo != null) {
                            planInfo = null; // 게시물이 있으면 계획 대신 표시
                        }
                    }
                }
                monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, postInfo));
            }
            // 미래 데이터 처리
            else {
                monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, null));
            }


            //  게시물이 존재하는 경우에만 공부 시간 및 투두리스트 정보 추가
            if (post != null) {
                List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, currentDate);
                int totalTodos = todos.size();
                int completedTodos = (int) todos.stream()
                        .filter(todo -> todo.getStatus() == TodoListStatus.DONE)
                        .count();
                double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

                int studyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, currentDate)
                        .stream()
                        .mapToInt(time -> time.getDuration())
                        .sum();

                dateSummaries.add(new HomeResponseDTO.DateSummaryInfo(
                        currentDate, studyTime, totalTodos, completedTodos, completionRate
                ));
            }
        }

//        // 날짜별 공부 시간 및 투두리스트 달성률 계산(게시물이 있던 없던 모든 시간 데이터가 뜨는 코드)
//        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = homeConverter.calculateDateSummaries(
//                userId, monthStart, monthEnd, timeRepository, todoRepository
//        );


        return new HomeResponseDTO.HomeMonthResponseDTO(
                monthlyData,
                dateSummaries
        );
    }

    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getFriendHomeMonthData(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.DailyData> monthlyData = new ArrayList<>();
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = new ArrayList<>();

        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            Plan plan = allPlans.stream()
                    .filter(p -> p.getStartTime().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Plan::getStartTime)) // 📌 가장 빠른 일정 선택
                    .orElse(null);

            Post post = allPosts.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Post::getCreatedAt)) // 📌 첫 게시물 선택
                    .orElse(null);

            HomeResponseDTO.PlanInfo planInfo = (plan != null)
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0)
                    : null;

            HomeResponseDTO.PostInfo postInfo = (post != null)
                    ? homeConverter.convertPostsToPostInfos(List.of(post)).get(0)
                    : null;

            monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, postInfo));
        }

        return new HomeResponseDTO.HomeMonthResponseDTO(monthlyData, dateSummaries);
    }


    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getFriendHomeMonthDataFor(Long userId, int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.DailyData> monthlyData = new ArrayList<>();
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = new ArrayList<>();

        for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            Plan plan = allPlans.stream()
                    .filter(p -> p.getStartTime().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Plan::getStartTime)) // 📌 가장 빠른 일정 선택
                    .orElse(null);

            Post post = allPosts.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(currentDate))
                    .min(Comparator.comparing(Post::getCreatedAt)) // 📌 첫 게시물 선택
                    .orElse(null);

            HomeResponseDTO.PlanInfo planInfo = (plan != null)
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0)
                    : null;

            HomeResponseDTO.PostInfo postInfo = (post != null)
                    ? homeConverter.convertPostsToPostInfos(List.of(post)).get(0)
                    : null;

            monthlyData.add(new HomeResponseDTO.DailyData(currentDate, planInfo, postInfo));
        }

        return new HomeResponseDTO.HomeMonthResponseDTO(monthlyData, dateSummaries);
    }


    @Override
    public HomeResponseDTO.FollowResponseDTO getFriendHomeFollowData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int followerCount = followRepository.countFollowers(userId);
        int followingCount = followRepository.countFollowing(userId);
        String profileImage = user.getProfileImage();

        return new HomeResponseDTO.FollowResponseDTO(followerCount, followingCount, profileImage,user.getAccountId());
    }


}
