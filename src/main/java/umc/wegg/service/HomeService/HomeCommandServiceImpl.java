package umc.wegg.service.HomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.HomeConverter;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Post;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.User;
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
    public HomeResponseDTO.HomeWeekResponseDTO getHomeWeekData() {
        Long userId = 1L; // 테스트용 userId

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY);

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.PlanInfo> weeklyPlans = new ArrayList<>();
        List<HomeResponseDTO.PostInfo> weeklyPosts = new ArrayList<>();

        for (Plan plan : allPlans) {
            LocalDate planDate = plan.getStartTime().toLocalDate();
            Optional<Post> matchingPost = allPosts.stream()
                    .filter(post -> post.getCreatedAt().toLocalDate().equals(planDate))
                    .findFirst();

            if (planDate.isBefore(today)) {
                matchingPost.ifPresent(post -> weeklyPosts.add(homeConverter.convertPostsToPostInfos(List.of(post)).get(0)));
            } else if (planDate.isAfter(today)) {
                weeklyPlans.add(homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0));
            } else {
                weeklyPlans.add(homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0));
                matchingPost.ifPresent(post -> weeklyPosts.add(homeConverter.convertPostsToPostInfos(List.of(post)).get(0)));
            }
        }

        // 오늘 날짜의 투두리스트 가져오기
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        List<HomeResponseDTO.TodoInfo> todayTodos = homeConverter.convertTodosToTodoInfos(todos);

        int totalTodos = todayTodos.size();
        int completedTodos = (int) todayTodos.stream().filter(HomeResponseDTO.TodoInfo::isCompleted).count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        int successCount = Optional.ofNullable(userRepository.findSuccessCountByUserId(userId)).orElse(0);
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream().mapToInt(time -> time.getDuration()).sum();

        // 📌 가장 최근의 계획 찾기
        LocalDateTime now = LocalDateTime.now();
        Plan closestPlan = allPlans.stream()
                .filter(plan -> plan.getStartTime().isAfter(now))
                .min(Comparator.comparing(Plan::getStartTime))
                .orElse(null);

        String upcomingPlanAddress = null;

        if (closestPlan != null) {
            long minutesUntilStart = java.time.Duration.between(now, closestPlan.getStartTime()).toMinutes();
            if (minutesUntilStart <= 10) {
                upcomingPlanAddress = closestPlan.getAddress();
            }
        }

        // 📌 포인트 지급 로직 추가
        int availablePoints = 0;
        boolean canReceivePoints = false;

        // 가장 최근에 포인트를 받은 successCount 조회 (DB 또는 저장소에서 가져와야 함)
        Integer lastReceivedSuccessCount = userRepository.findLastReceivedSuccessCount(userId).orElse(0);

        // 현재 successCount 중에서 3의 배수인 것 중, 아직 받지 않은 것 찾기
        for (int i = lastReceivedSuccessCount + 3; i <= successCount; i += 3) {
            availablePoints += 3;
        }

        if (availablePoints > 0) {
            canReceivePoints = true;
        }

        // ✅ 빌더 패턴 사용하여 DTO 생성
        return HomeResponseDTO.HomeWeekResponseDTO.builder()
                .weeklyPlans(weeklyPlans)
                .weeklyPosts(weeklyPosts)
                .todayTodos(todayTodos)
                .totalTodos(totalTodos)
                .completedTodos(completedTodos)
                .completionRate(completionRate)
                .successCount(successCount)
                .totalStudyTime(totalStudyTime)
                .upcomingPlanAddress(upcomingPlanAddress) // 📌 10분 남은 일정이 있으면 추가
                .availablePoints(availablePoints) // 📌 받을 수 있는 포인트
                .canReceivePoints(canReceivePoints) // 📌 포인트 받을 수 있는지 여부
                .build();
    }


    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getHomeMonthData() {
        Long userId = 1L; // 테스트를 위해 userId를 1로 설정

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.PlanInfo> monthlyPlans = new ArrayList<>();
        List<HomeResponseDTO.PostInfo> monthlyPosts = new ArrayList<>();

        for (Plan plan : allPlans) {
            LocalDate planDate = plan.getStartTime().toLocalDate();
            Optional<Post> matchingPost = allPosts.stream()
                    .filter(post -> post.getCreatedAt().toLocalDate().equals(planDate))
                    .findFirst();

            if (planDate.isBefore(today)) {
                matchingPost.ifPresent(post -> monthlyPosts.add(homeConverter.convertPostsToPostInfos(List.of(post)).get(0)));
            } else if (planDate.isAfter(today)) {
                monthlyPlans.add(homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0));
            } else {
                monthlyPlans.add(homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0));
                matchingPost.ifPresent(post -> monthlyPosts.add(homeConverter.convertPostsToPostInfos(List.of(post)).get(0)));
            }
        }

        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = homeConverter.calculateDateSummaries(
                userId, monthStart, monthEnd, timeRepository, todoRepository
        );

        int totalTodos = todoRepository.findTodosByUserIdAndDate(userId, today).size();
        int completedTodos = (int) todoRepository.findTodosByUserIdAndDate(userId, today)
                .stream()
                .filter(todo -> todo.getStatus() == TodoListStatus.DONE)
                .count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        int successCount = Optional.ofNullable(userRepository.findSuccessCountByUserId(userId)).orElse(0);
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream()
                .mapToInt(time -> time.getDuration())
                .sum();

        return new HomeResponseDTO.HomeMonthResponseDTO(
                monthlyPlans,
                monthlyPosts,
                dateSummaries,
                totalTodos,
                completedTodos,
                completionRate,
                successCount,
                totalStudyTime
        );
    }


    @Override
    public HomeResponseDTO.FollowResponseDTO getHomeFollowData() {
        Long userId = 1L; // 테스트를 위해 userId를 1로 설정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 팔로워/팔로잉 수 계산
        int followerCount = followRepository.countFollowers(userId);
        int followingCount = followRepository.countFollowing(userId);

        String profileImage = user.getProfileImage();

        return new HomeResponseDTO.FollowResponseDTO(
                followerCount,
                followingCount,
                profileImage
        );
    }

    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getHomeMonthDataFor(int year, int month) {
        Long userId = 1L; // 테스트용 userId
        LocalDate today = LocalDate.now();
        LocalDate monthStart = LocalDate.of(year, month, 1); // 해당 달 첫째 날
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth()); // 해당 달 마지막 날

        // 해당 월의 일정 및 게시물 조회
        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<HomeResponseDTO.PlanInfo> monthlyPlans = new ArrayList<>();
        List<HomeResponseDTO.PostInfo> monthlyPosts = new ArrayList<>();

        for (Plan plan : allPlans) {
            LocalDate planDate = plan.getStartTime().toLocalDate();
            Optional<Post> matchingPost = allPosts.stream()
                    .filter(post -> post.getCreatedAt().toLocalDate().equals(planDate))
                    .findFirst();

            if (planDate.isBefore(today)) {
                // 과거 일정 -> `post`가 존재할 경우만 추가
                matchingPost.ifPresent(post -> monthlyPosts.add(homeConverter.convertPostsToPostInfos(List.of(post)).get(0)));
            } else if (planDate.isAfter(today)) {
                // 미래 일정 -> `plan` 추가
                monthlyPlans.add(homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0));
            } else {
                // 오늘 일정 -> `plan` 추가
                monthlyPlans.add(homeConverter.convertPlansToPlanInfos(List.of(plan)).get(0));
                // `post`가 존재하면 `post` 리스트에도 추가
                matchingPost.ifPresent(post -> monthlyPosts.add(homeConverter.convertPostsToPostInfos(List.of(post)).get(0)));
            }
        }

        // 날짜별 공부 시간 및 투두리스트 달성률 계산
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = homeConverter.calculateDateSummaries(
                userId, monthStart, monthEnd, timeRepository, todoRepository
        );

        // 오늘 날짜의 투두리스트 가져오기
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        int totalTodos = todos.size();
        int completedTodos = (int) todos.stream().filter(todo -> todo.getStatus() == TodoListStatus.DONE).count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        // 인증 성공 횟수 계산
        int successCount = Optional.ofNullable(userRepository.findSuccessCountByUserId(userId)).orElse(0);

        // 오늘 날짜의 공부 시간 합산
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream().mapToInt(time -> time.getDuration()).sum();

        return new HomeResponseDTO.HomeMonthResponseDTO(
                monthlyPlans,
                monthlyPosts,
                dateSummaries,
                totalTodos,
                completedTodos,
                completionRate,
                successCount,
                totalStudyTime
        );
    }

}
