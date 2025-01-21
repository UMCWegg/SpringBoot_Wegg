package umc.wegg.service.HomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.converter.HomeConverter;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    public HomeResponseDTO getHomeWeekData() {
        Long userId = 1L; // 테스트를 위해 userId를 1로 설정
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY); // 이번 주 월요일
        LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY); // 이번 주 일요일

        // 주간 데이터 변환
        List<HomeResponseDTO.PlanInfo> weeklyPlans = homeConverter.convertPlansToPlanInfos(
                planRepository.findPlansByUserIdBetween(userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX))
        );
        List<HomeResponseDTO.PostInfo> weeklyPosts = homeConverter.convertPostsToPostInfos(
                postRepository.findPostsByUserIdBetween(userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX))
        );

        // 투두리스트 통계
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        int totalTodos = todos.size();
        int completedTodos = (int) todos.stream().filter(todo -> todo.getStatus() == TodoListStatus.DONE).count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        // 팔로워/팔로잉 수 계산
        int followerCount = followRepository.countFollowers(user.getId());
        int followingCount = followRepository.countFollowing(user.getId());

        // 인증 성공 횟수 계산
        int successCount = userRepository.findSuccessCountByUserId(userId);
        // 오늘 날짜의 공부 시간 합산
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream().mapToInt(time -> time.getDuration()).sum();

        return new HomeResponseDTO(
                weeklyPlans,
                weeklyPosts,
                List.of(),
                totalTodos,
                completedTodos,
                completionRate,
                successCount,
                totalStudyTime,
                followerCount,
                followingCount
        );
    }

    @Override
    public HomeResponseDTO getHomeMonthData() {
        Long userId = 1L; // 테스트를 위해 userId를 1로 설정
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1); // 이번 달 첫째 날
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth()); // 이번 달 마지막 날

        // 월간 데이터 변환
        List<HomeResponseDTO.PlanInfo> monthlyPlans = homeConverter.convertPlansToPlanInfos(
                planRepository.findPlansByUserIdBetween(userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX))
        );
        List<HomeResponseDTO.PostInfo> monthlyPosts = homeConverter.convertPostsToPostInfos(
                postRepository.findPostsByUserIdBetween(userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX))
        );
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = homeConverter.calculateDateSummaries(
                userId, monthStart, monthEnd, timeRepository, todoRepository
        );

        // 투두리스트 통계
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        int totalTodos = todos.size();
        int completedTodos = (int) todos.stream().filter(todo -> todo.getStatus() == TodoListStatus.DONE).count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        // 팔로워/팔로잉 수 계산
        int followerCount = followRepository.countFollowers(user.getId());
        int followingCount = followRepository.countFollowing(user.getId());

        // 인증 성공 횟수 계산
        int successCount = userRepository.findSuccessCountByUserId(userId);
        // 오늘 날짜의 공부 시간 합산
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream().mapToInt(time -> time.getDuration()).sum();

        return new HomeResponseDTO(
                monthlyPlans,
                monthlyPosts,
                dateSummaries,
                totalTodos,
                completedTodos,
                completionRate,
                successCount,
                totalStudyTime,
                followerCount,
                followingCount
        );
    }

    @Override
    public HomeResponseDTO getHomeMonthDataFor(int year, int month) {
        Long userId = 1L; // 테스트를 위해 userId를 1로 설정
        LocalDate monthStart = LocalDate.of(year, month, 1); // 해당 달 첫째 날
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth()); // 해당 달 마지막 날

        // 월간 데이터 변환
        List<HomeResponseDTO.PlanInfo> monthlyPlans = homeConverter.convertPlansToPlanInfos(
                planRepository.findPlansByUserIdBetween(userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX))
        );
        List<HomeResponseDTO.PostInfo> monthlyPosts = homeConverter.convertPostsToPostInfos(
                postRepository.findPostsByUserIdBetween(userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX))
        );
        List<HomeResponseDTO.DateSummaryInfo> dateSummaries = homeConverter.calculateDateSummaries(
                userId, monthStart, monthEnd, timeRepository, todoRepository
        );

        // 투두리스트 통계
        LocalDate today = LocalDate.now();
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        int totalTodos = todos.size();
        int completedTodos = (int) todos.stream().filter(todo -> todo.getStatus() == TodoListStatus.DONE).count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0;

        // 팔로워/팔로잉 수 계산
        int followerCount = followRepository.countFollowers(userId);
        int followingCount = followRepository.countFollowing(userId);

        // 인증 성공 횟수 계산
        int successCount = userRepository.findSuccessCountByUserId(userId);

        // 오늘 날짜의 공부 시간 합산
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream().mapToInt(time -> time.getDuration()).sum();

        return new HomeResponseDTO(
                monthlyPlans,
                monthlyPosts,
                dateSummaries,
                totalTodos,
                completedTodos,
                completionRate,
                successCount,
                totalStudyTime,
                followerCount,
                followingCount
        );
    }

}
