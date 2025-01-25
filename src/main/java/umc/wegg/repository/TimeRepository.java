package umc.wegg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.wegg.domain.Time;

import java.time.LocalDate;
import java.util.List;

public interface TimeRepository extends JpaRepository<Time, Long> {

    // 특정 날짜의 공부 시간 검색
//    @Query("SELECT t FROM Time t WHERE DATE(t.createdAt) = :date")
//    List<Time> findStudyTimeByDate(LocalDate date);
    @Query("SELECT t FROM Time t WHERE t.user.id = :userId AND DATE(t.date) = :date")
    List<Time> findStudyTimeByUserIdAndDate(@Param("userId") Long userId,
                                            @Param("date")LocalDate date);

}
