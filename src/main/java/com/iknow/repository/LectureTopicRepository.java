package com.iknow.repository;

import com.iknow.entity.LectureTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureTopicRepository extends JpaRepository<LectureTopic, Long> {

    List<LectureTopic> findBySessionIdOrderByCapturedAtDesc(String sessionId);

    // confused 발생 시각 기준 가장 가까운(이전) 강의 토픽 조회
    Optional<LectureTopic> findTopBySessionIdAndCapturedAtLessThanEqualOrderByCapturedAtDesc(
            String sessionId, LocalDateTime capturedAt);
}
