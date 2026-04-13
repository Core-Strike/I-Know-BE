package com.iknow.repository;

import com.iknow.entity.UnderstandingDifficultyTrend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UnderstandingDifficultyTrendRepository extends JpaRepository<UnderstandingDifficultyTrend, Long> {
    List<UnderstandingDifficultyTrend> findByCapturedAtBetween(LocalDateTime start, LocalDateTime end);
    List<UnderstandingDifficultyTrend> findByCapturedAtBetweenAndCurriculum(LocalDateTime start, LocalDateTime end, String curriculum);
}
