package com.iknow.repository;

import com.iknow.entity.LearningSignalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningSignalEventRepository extends JpaRepository<LearningSignalEvent, Long> {
    List<LearningSignalEvent> findBySessionIdOrderByCapturedAtDesc(String sessionId);
    List<LearningSignalEvent> findByCapturedAtBetween(LocalDateTime start, LocalDateTime end);
    List<LearningSignalEvent> findByCapturedAtBetweenAndCurriculum(LocalDateTime start, LocalDateTime end, String curriculum);
}
