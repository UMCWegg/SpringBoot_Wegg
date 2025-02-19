package umc.wegg.service.HomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.HomeConverter;
import umc.wegg.domain.*;
import umc.wegg.domain.enums.FollowStatus;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.enums.TodoListStatus;
import umc.wegg.domain.mapping.Follow;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.repository.*;
import umc.wegg.service.EggService.EggServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final EggRepository eggRepository;
    private final EggServiceImpl eggService;

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
        List<Egg> allEggs = allPlans.stream()
                .map(plan -> eggRepository.findByPlanId(plan.getId()).orElse(null)) // 각 Plan의 Egg 조회
                .filter(egg -> egg != null) // NULL 값 제외
                .collect(Collectors.toList());

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
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan), allEggs).get(0)
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

        // 가장 가까운 일정 찾기 (시작 시간이 가장 가까운 계획)
        Plan closestPlan = allPlans.stream()
                .filter(plan -> plan.getStartTime().isAfter(now))
                .min(Comparator.comparing(Plan::getStartTime))
                .orElse(null);

        Long planId = null;
        String upcomingPlanAddress = null;

        // 현재 시간이 어떤 일정의 startTime과 finishTime 사이에 있다면 해당 계획을 사용
        Plan activePlan = allPlans.stream()
                .filter(plan -> now.isAfter(plan.getStartTime()) && now.isBefore(plan.getFinishTime()))
                .findFirst()
                .orElse(null);

        if (activePlan != null) {
            LocalDateTime planStartTime = activePlan.getStartTime();
            LocalDateTime planFinishTime = activePlan.getFinishTime();
            LocalDateTime planRandomTime = activePlan.getRandomTime();
            LocalDateTime planLateTime = planStartTime.plusMinutes(activePlan.getLateTime().getMinutes());

            // startTime 5분 전부터 + 지각 허용 시간까지 upcomingPlanAddress 포함
            if (now.isAfter(planStartTime.minusMinutes(5)) && now.isBefore(planLateTime)) {
                upcomingPlanAddress = activePlan.getAddress().getAddress();
                planId = activePlan.getId();
            }

            // 랜덤 타임이 설정된 경우 5분 전부터 randomTime까지 포함
            if (planRandomTime != null) {
                if (now.isAfter(planRandomTime.minusMinutes(5)) && now.isBefore(planRandomTime)) {
                    upcomingPlanAddress = activePlan.getAddress().getAddress();
                    planId = activePlan.getId();
                }
            }
        } else if (closestPlan != null) {
            LocalDateTime planStartTime = closestPlan.getStartTime();
            LocalDateTime planLateTime = planStartTime.plusMinutes(closestPlan.getLateTime().getMinutes());

            // startTime 5분 전부터 + 지각 허용 시간까지 upcomingPlanAddress 포함
            if (now.isAfter(planStartTime.minusMinutes(5)) && now.isBefore(planLateTime)) {
                upcomingPlanAddress = closestPlan.getAddress().getAddress();
                planId = closestPlan.getId();
            }

            // 랜덤 타임이 설정된 경우 5분 전부터 randomTime까지 포함
            LocalDateTime planRandomTime = closestPlan.getRandomTime();
            if (planRandomTime != null) {
                if (now.isAfter(planRandomTime.minusMinutes(5)) && now.isBefore(planRandomTime)) {
                    upcomingPlanAddress = closestPlan.getAddress().getAddress();
                    planId = closestPlan.getId();
                }
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
                .planId(planId)
                .upcomingPlanAddress(upcomingPlanAddress)
                .availablePoints(availablePoints)
                .canReceivePoints(canReceivePoints)
                .build();
    }


    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getHomeMonthData(AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId(); // 로그인된 사용자 ID
        LocalDate today = LocalDate.now();
        // ✅ 본인의 월간 데이터를 조회할 때만 Egg 상태 초기화 실행
        HomeResponseDTO.HomeMonthResponseDTO response = getMonthData(userId, today.getYear(), today.getMonthValue());
        eggService.scheduleResetEggStatus(userId);

        return response;
    }


    @Override
    public HomeResponseDTO.FollowResponseDTO getHomeFollowData(AuthenticatedUser authenticatedUser) {
        Long userId = authenticatedUser.getUserId();
        return getFollowData(userId);
    }

    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getHomeMonthDataFor(AuthenticatedUser authenticatedUser, int year, int month) {
        Long userId = authenticatedUser.getUserId();
        return getMonthData(userId, year, month);
    }

    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getFriendHomeMonthData(Long userId) {
        LocalDate today = LocalDate.now();
        return getMonthData(userId, today.getYear(), today.getMonthValue());
    }


    @Override
    public HomeResponseDTO.HomeMonthResponseDTO getFriendHomeMonthDataFor(Long userId, int year, int month) {
        return getMonthData(userId, year, month);
    }


    @Override
    public HomeResponseDTO.FollowResponseDTO getFriendHomeFollowData(Long userId, AuthenticatedUser authenticatedUser) {
        // 현재 로그인한 사용자 ID
        Long currentUserId = authenticatedUser.getUserId();

        // ✅ 현재 사용자(User)와 친구(User) 객체 가져오기
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다."));
        User friendUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("친구 사용자를 찾을 수 없습니다."));

        // ✅ 현재 사용자와 친구의 팔로우 상태 조회
        Follow follow = followRepository.findByFollowerAndFollowee(currentUser, friendUser)
                .orElse(null);  // 해당 팔로우 관계가 없으면 null 반환

        FollowStatus followStatus;
        if (follow != null) {
            followStatus = follow.getFollowStatus(); // 기존 팔로우 관계가 있다면 가져오기
        } else {
            followStatus = null; // 팔로우 관계가 없을 경우
        }

        // 기존 getFollowData 로직을 활용하여 기본 데이터 가져오기
        HomeResponseDTO.FollowResponseDTO baseResponse = getFollowData(userId);

        // 새로운 DTO로 생성 (팔로우 상태 추가)
        return new HomeResponseDTO.FollowResponseDTO(
                baseResponse.getFollowerCount(),
                baseResponse.getFollowingCount(),
                baseResponse.getProfileImage(),
                baseResponse.getAccountId(),
                followStatus // ✅ 친구 홈에서만 추가되는 필드
        );
    }

    private HomeResponseDTO.HomeMonthResponseDTO getMonthData(Long userId, int year, int month) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        List<Plan> allPlans = planRepository.findPlansByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );
        List<Post> allPosts = postRepository.findPostsByUserIdBetween(
                userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)
        );

        List<Egg> allEggs = allPlans.stream()
                .map(plan -> eggRepository.findByPlanId(plan.getId()).orElse(null)) // 각 Plan의 Egg 조회
                .filter(egg -> egg != null) // NULL 값 제외
                .collect(Collectors.toList());


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
                    ? homeConverter.convertPlansToPlanInfos(List.of(plan), allEggs).get(0)
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

            //  실패한 일정이 있는 경우 dateSummaries에도 반영
            boolean hasFailedPlan = plan != null && plan.getStatus() == PlanStatus.FAILED;

            //  게시물이 존재하는 경우에만 공부 시간 및 투두리스트 정보 추가
            if (post != null || plan != null && plan.getStatus() == PlanStatus.FAILED) {
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
                        currentDate, studyTime, completionRate, hasFailedPlan
                ));
            }

        }

        return new HomeResponseDTO.HomeMonthResponseDTO(
                monthlyData,
                dateSummaries
        );
    }

    private HomeResponseDTO.FollowResponseDTO getFollowData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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




}
