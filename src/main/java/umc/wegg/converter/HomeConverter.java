package umc.wegg.converter;

import org.springframework.stereotype.Component;
import umc.wegg.domain.Plan;
import umc.wegg.domain.Post;
import umc.wegg.dto.HomeResponseDTO;
import umc.wegg.repository.TimeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HomeConverter {

    // Plan -> PlanInfo 변환
    public List<HomeResponseDTO.PlanInfo> convertPlansToPlanInfos(List<Plan> plans) {
        return plans.stream()
                .map(plan -> new HomeResponseDTO.PlanInfo(
                        plan.getId(),
                        plan.getStartTime(),
                        plan.getFinishTime()
                ))
                .collect(Collectors.toList());
    }

    // Post -> PostInfo 변환
    public List<HomeResponseDTO.PostInfo> convertPostsToPostInfos(List<Post> posts) {
        return posts.stream()
                .map(post -> new HomeResponseDTO.PostInfo(
                        post.getId(),
                        post.getImageUrl(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 날짜별 공부 시간 계산
    public List<HomeResponseDTO.TimeInfo> calculateTimeInfos(Long userId, LocalDate start, LocalDate end, TimeRepository timeRepository) {
        return start.datesUntil(end.plusDays(1)) // 시작일부터 종료일까지 반복
                .map(date -> {
                    int studyTime = timeRepository.findStudyTimeByUserIdAndDate(userId, date)
                            .stream()
                            .mapToInt(time -> time.getDuration())
                            .sum();
                    return new HomeResponseDTO.TimeInfo(date, studyTime);
                })
                .collect(Collectors.toList());
    }
}

