package com.iknow.repository;

import com.iknow.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findBySessionIdOrderByCapturedAtDesc(String sessionId);
    List<Alert> findByCapturedAtBetweenOrderByCapturedAtDesc(LocalDateTime start, LocalDateTime end);
    List<Alert> findByCapturedAtBetweenAndCurriculumOrderByCapturedAtDesc(LocalDateTime start, LocalDateTime end, String curriculum);
}
