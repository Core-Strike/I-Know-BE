package com.iknow.service;

import com.iknow.dto.response.AlertResponse;
import com.iknow.dto.response.DashboardClassResponse;
import com.iknow.entity.Alert;
import com.iknow.entity.Session;
import com.iknow.repository.AlertRepository;
import com.iknow.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SessionRepository sessionRepository;
    private final AlertRepository alertRepository;

    @Transactional(readOnly = true)
    public List<DashboardClassResponse> getDashboardClasses() {

        // 전체 세션을 classId 기준으로 그룹화
        Map<String, List<Session>> sessionsByClass = sessionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        s -> s.getClassId() != null ? s.getClassId() : "unknown"
                ));

        return sessionsByClass.entrySet().stream()
                .map(entry -> buildClassResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private DashboardClassResponse buildClassResponse(String classId, List<Session> sessions) {

        // 해당 반의 모든 Alert 수집
        List<Alert> alerts = sessions.stream()
                .flatMap(s -> alertRepository
                        .findBySessionIdOrderByCapturedAtDesc(s.getSessionId()).stream())
                .collect(Collectors.toList());

        // 평균 confusedScore
        double avgScore = alerts.stream()
                .filter(a -> a.getConfusedScore() != null)
                .mapToDouble(Alert::getConfusedScore)
                .average()
                .orElse(0.0);

        // unclearTopic 빈도 상위 5개 (대시보드 태그 목록)
        List<String> topTopics = alerts.stream()
                .filter(a -> a.getUnclearTopic() != null)
                .collect(Collectors.groupingBy(Alert::getUnclearTopic, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 최근 알림 10개
        List<AlertResponse> recentAlerts = alerts.stream()
                .limit(10)
                .map(AlertResponse::from)
                .collect(Collectors.toList());

        return DashboardClassResponse.builder()
                .classId(classId)
                .alertCount(alerts.size())
                .avgConfusedScore(avgScore)
                .topTopics(topTopics)
                .recentAlerts(recentAlerts)
                .build();
    }
}
