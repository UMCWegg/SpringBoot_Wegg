package umc.wegg.service.HomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.TodoList;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.dto.HomeResponseDTO.PlanInfo;
import umc.wegg.dto.HomeResponseDTO.PostInfo;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.PostRepository;
import umc.wegg.repository.TodoRepository;
import umc.wegg.repository.TimeRepository;
import umc.wegg.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeCommandServiceImpl implements HomeCommandService {

    private final PlanRepository planRepository;
    private final PostRepository postRepository;
    private final TodoRepository todoRepository;
    private final TimeRepository timeRepository;
    private final UserRepository userRepository;

    @Override
    public HomeResponseDTO getHomeData() {
        Long userId = 1L; // 테스트를 위해 userId를 1로 설정

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY); // 이번 주 월요일
        LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY); // 이번 주 일요일

        // 주간 캘린더: 월~일 동안의 일정들 (userId 기준으로 필터링)
        List<PlanInfo> weeklyPlans = planRepository.findPlansByUserIdBetween(userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX))
                .stream()
                .map(plan -> new PlanInfo(
                        plan.getId(),
                        plan.getStartTime(),
                        plan.getFinishTime()
                ))
                .collect(Collectors.toList());

        // 주간 게시물: 월~일 동안의 게시물들 (userId 기준으로 필터링)
        List<PostInfo> weeklyPosts = postRepository.findPostsByUserIdBetween(userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX))
                .stream()
                .map(post -> new PostInfo(
                        post.getId(),
                        post.getImageUrl(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 오늘 날짜의 투두리스트들 (userId 기준으로 필터링)
        List<TodoList> todos = todoRepository.findTodosByUserIdAndDate(userId, today);
        int totalTodos = todos.size(); // 총 투두리스트 항목 수
        int completedTodos = (int) todos.stream()
                .filter(todo -> todo.getStatus() == TodoListStatus.DONE) // 완료 상태인지 확인
                .count();
        double completionRate = totalTodos > 0 ? ((double) completedTodos / totalTodos) * 100 : 0.0; // 오늘의 달성률

        // 인증 성공 횟수 계산 (userId 기준)
        int successCount = userRepository.findSuccessCountByUserId(userId);
        int earnedPoints = (successCount / 3) * 10; // 인증 성공 횟수 3의 배수일 때 포인트 계산

        // 오늘 날짜의 공부 시간 합산 (userId 기준)
        int totalStudyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, today)
                .stream()
                .mapToInt(time -> time.getDuration())
                .sum();

        // DTO 반환
        return new HomeResponseDTO(
                weeklyPlans,
                weeklyPosts,
                totalTodos,
                completedTodos,
                completionRate,
                successCount,
                totalStudyTime
        );
    }
}

