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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SessionRepository sessionRepository;
    private final AlertRepository alertRepository;

    @Transactional(readOnly = true)
    public List<DashboardClassResponse> getDashboardClasses(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Alert> alertsForDate = alertRepository.findByCapturedAtBetweenOrderByCapturedAtDesc(startOfDay, endOfDay);

        Set<String> sessionIds = alertsForDate.stream()
                .map(Alert::getSessionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Session> sessionBySessionId = sessionRepository.findAllBySessionIdIn(sessionIds)
                .stream()
                .collect(Collectors.toMap(Session::getSessionId, session -> session));

        Map<String, List<Alert>> alertsByClass = alertsForDate.stream()
                .collect(Collectors.groupingBy(alert -> {
                    Session session = sessionBySessionId.get(alert.getSessionId());
                    return session != null && session.getClassId() != null ? session.getClassId() : "unknown";
                }));

        return alertsByClass.entrySet().stream()
                .map(entry -> buildClassResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private DashboardClassResponse buildClassResponse(String classId, List<Alert> alerts) {
        double avgScore = alerts.stream()
                .filter(a -> a.getConfusedScore() != null)
                .mapToDouble(Alert::getConfusedScore)
                .average()
                .orElse(0.0);

        List<String> topTopics = alerts.stream()
                .filter(a -> a.getUnclearTopic() != null)
                .collect(Collectors.groupingBy(Alert::getUnclearTopic, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

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
