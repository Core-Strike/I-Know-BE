package com.iknow.service;

import com.iknow.dto.response.AlertResponse;
import com.iknow.dto.response.DifficultyTrendPointResponse;
import com.iknow.dto.response.DashboardClassResponse;
import com.iknow.dto.response.KeywordReportResponse;
import com.iknow.dto.response.SignalBreakdownResponse;
import com.iknow.entity.Alert;
import com.iknow.entity.AlertKeyword;
import com.iknow.entity.LearningSignalEvent;
import com.iknow.entity.Session;
import com.iknow.entity.SessionParticipant;
import com.iknow.entity.UnderstandingDifficultyTrend;
import com.iknow.repository.AlertKeywordRepository;
import com.iknow.repository.AlertRepository;
import com.iknow.repository.LearningSignalEventRepository;
import com.iknow.repository.SessionParticipantRepository;
import com.iknow.repository.SessionRepository;
import com.iknow.repository.UnderstandingDifficultyTrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final SessionRepository sessionRepository;
    private final AlertRepository alertRepository;
    private final AlertKeywordRepository alertKeywordRepository;
    private final LearningSignalEventRepository learningSignalEventRepository;
    private final SessionParticipantRepository sessionParticipantRepository;
    private final UnderstandingDifficultyTrendRepository understandingDifficultyTrendRepository;

    @Transactional(readOnly = true)
    public List<DashboardClassResponse> getDashboardClasses(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Alert> alertsForDate = alertRepository.findByCapturedAtBetweenOrderByCapturedAtDesc(startOfDay, endOfDay);
        List<LearningSignalEvent> signalsForDate = learningSignalEventRepository.findByCapturedAtBetween(startOfDay, endOfDay);
        List<SessionParticipant> participantsForDate = sessionParticipantRepository.findByJoinedAtBetween(startOfDay, endOfDay);
        List<UnderstandingDifficultyTrend> difficultyTrendsForDate =
                understandingDifficultyTrendRepository.findByCapturedAtBetween(startOfDay, endOfDay);

        Set<String> sessionIds = new LinkedHashSet<>();
        alertsForDate.stream()
                .map(Alert::getSessionId)
                .filter(Objects::nonNull)
                .forEach(sessionIds::add);
        signalsForDate.stream()
                .map(LearningSignalEvent::getSessionId)
                .filter(Objects::nonNull)
                .forEach(sessionIds::add);
        difficultyTrendsForDate.stream()
                .map(UnderstandingDifficultyTrend::getSessionId)
                .filter(Objects::nonNull)
                .forEach(sessionIds::add);

        Map<String, Session> sessionBySessionId = sessionRepository.findAllBySessionIdIn(sessionIds)
                .stream()
                .collect(Collectors.toMap(Session::getSessionId, session -> session));
        Map<String, SessionParticipant> participantSnapshotBySessionId = participantsForDate.stream()
                .filter(participant -> participant.getSessionId() != null)
                .collect(Collectors.toMap(
                        SessionParticipant::getSessionId,
                        participant -> participant,
                        (first, second) -> first
                ));

        Map<ClassGroupingKey, List<Alert>> alertsByClass = alertsForDate.stream()
                .collect(Collectors.groupingBy(alert -> resolveClassGroupingKey(
                        alert.getSessionId(),
                        alert.getCurriculum(),
                        alert.getClassId(),
                        sessionBySessionId,
                        participantSnapshotBySessionId
                )));
        Map<ClassGroupingKey, List<LearningSignalEvent>> signalsByClass = signalsForDate.stream()
                .collect(Collectors.groupingBy(signal -> resolveClassGroupingKey(
                        signal.getSessionId(),
                        signal.getCurriculum(),
                        signal.getClassId(),
                        sessionBySessionId,
                        participantSnapshotBySessionId
                )));
        Map<ClassGroupingKey, List<UnderstandingDifficultyTrend>> difficultyTrendsByClass = difficultyTrendsForDate.stream()
                .collect(Collectors.groupingBy(trend -> resolveClassGroupingKey(
                        trend.getSessionId(),
                        trend.getCurriculum(),
                        trend.getClassId(),
                        sessionBySessionId,
                        participantSnapshotBySessionId
                )));

        Set<ClassGroupingKey> groupingKeys = new LinkedHashSet<>();
        groupingKeys.addAll(alertsByClass.keySet());
        groupingKeys.addAll(signalsByClass.keySet());
        groupingKeys.addAll(difficultyTrendsByClass.keySet());

        return groupingKeys.stream()
                .map(key -> buildClassResponse(
                        key,
                        alertsByClass.getOrDefault(key, List.of()),
                        signalsByClass.getOrDefault(key, List.of()),
                        difficultyTrendsByClass.getOrDefault(key, List.of()),
                        participantsForDate
                ))
                .sorted(Comparator.comparing(DashboardClassResponse::getCurriculum, Comparator.nullsLast(String::compareTo))
                        .thenComparing(DashboardClassResponse::getClassId, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    @Transactional(readOnly = true)
    public KeywordReportResponse getKeywordReport(LocalDate date, String keyword, String curriculum, String classId) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isBlank()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }

        FilteredAlertContext context = getFilteredAlertContext(date, curriculum, classId);
        List<Long> alertIds = context.alerts().stream()
                .map(Alert::getId)
                .filter(Objects::nonNull)
                .toList();

        if (alertIds.isEmpty()) {
            return buildKeywordReport(date, normalizedKeyword, curriculum, classId, List.of());
        }

        Set<Long> matchedAlertIds = alertKeywordRepository.findByAlertIdInAndKeyword(alertIds, normalizedKeyword).stream()
                .map(AlertKeyword::getAlertId)
                .collect(Collectors.toSet());

        List<Alert> matchedAlerts = context.alerts().stream()
                .filter(alert -> matchedAlertIds.contains(alert.getId()))
                .sorted(Comparator.comparing(Alert::getCapturedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        return buildKeywordReport(date, normalizedKeyword, curriculum, classId, matchedAlerts);
    }

    private KeywordReportResponse buildKeywordReport(
            LocalDate date,
            String keyword,
            String curriculum,
            String classId,
            List<Alert> matchedAlerts
    ) {
        String resolvedCurriculum = matchedAlerts.stream()
                .map(Alert::getCurriculum)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
        if (resolvedCurriculum.isBlank()) {
            resolvedCurriculum = curriculum;
        }

        String resolvedClassId = matchedAlerts.stream()
                .map(Alert::getClassId)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
        if (resolvedClassId.isBlank()) {
            resolvedClassId = classId;
        }

        int avgConfusion = matchedAlerts.isEmpty()
                ? 0
                : (int) Math.round(matchedAlerts.stream()
                .mapToInt(this::calculateConfusionPercent)
                .average()
                .orElse(0.0));
        int avgUnderstanding = Math.max(0, 100 - avgConfusion);
        int reinforcementNeed = avgConfusion;

        String reinforcementLevel = reinforcementNeed >= 70
                ? "높음"
                : reinforcementNeed >= 40
                ? "보통"
                : "낮음";

        String report = matchedAlerts.isEmpty()
                ? String.format("'%s' 키워지와 연결된 알림이 아직 없습니다.", keyword)
                : String.format("'%s' 관련 알림은 %d건이며 평균 이해도는 %d%%입니다. 보충 필요도는 %s(%d%%)로 판단됩니다.",
                keyword, matchedAlerts.size(), avgUnderstanding, reinforcementLevel, reinforcementNeed);

        List<String> occurrenceTimes = matchedAlerts.stream()
                .map(Alert::getCapturedAt)
                .filter(Objects::nonNull)
                .map(TIME_FORMATTER::format)
                .distinct()
                .limit(5)
                .toList();

        return KeywordReportResponse.builder()
                .keyword(keyword)
                .curriculum(resolvedCurriculum)
                .classId(resolvedClassId)
                .date(date.toString())
                .alertCount(matchedAlerts.size())
                .avgUnderstanding(avgUnderstanding)
                .reinforcementNeed(reinforcementNeed)
                .reinforcementLevel(reinforcementLevel)
                .report(report)
                .occurrenceTimes(occurrenceTimes)
                .build();
    }

    private DashboardClassResponse buildClassResponse(
            ClassGroupingKey key,
            List<Alert> alerts,
            List<LearningSignalEvent> signals,
            List<UnderstandingDifficultyTrend> difficultyTrends,
            List<SessionParticipant> participantsForDate
    ) {
        List<Long> alertIds = alerts.stream()
                .map(Alert::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, List<String>> keywordsByAlertId = alertKeywordRepository.findByAlertIdIn(alertIds).stream()
                .collect(Collectors.groupingBy(
                        AlertKeyword::getAlertId,
                        Collectors.mapping(AlertKeyword::getKeyword, Collectors.toList())
                ));

        long participantCount = participantsForDate.stream()
                .filter(participant -> key.curriculum().equals(participant.getCurriculum()))
                .filter(participant -> key.classId().equals(participant.getClassId()))
                .map(SessionParticipant::getStudentId)
                .filter(Objects::nonNull)
                .filter(studentId -> !studentId.isBlank())
                .distinct()
                .count();

        double avgScore = alerts.stream()
                .filter(a -> (a.getTotalStudentCount() != null && a.getTotalStudentCount() > 0) || a.getConfusedScore() != null)
                .mapToDouble(a -> calculateConfusionPercent(a) / 100.0)
                .average()
                .orElse(0.0);

        List<String> topTopics = alerts.stream()
                .filter(a -> a.getUnclearTopic() != null)
                .collect(Collectors.groupingBy(Alert::getUnclearTopic, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        List<AlertResponse> recentAlerts = alerts.stream()
                .map(alert -> AlertResponse.from(alert, keywordsByAlertId.getOrDefault(alert.getId(), List.of())))
                .toList();

        double signalDenominator = Math.max(1, signals.size());
        List<SignalBreakdownResponse> signalBreakdown = signals.stream()
                .collect(Collectors.groupingBy(LearningSignalEvent::getSignalType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> SignalBreakdownResponse.builder()
                        .signalType(entry.getKey())
                        .label(getSignalLabel(entry.getKey()))
                        .count(entry.getValue())
                        .ratio(entry.getValue() / signalDenominator)
                        .build())
                .toList();

        List<DifficultyTrendPointResponse> difficultyTrend = buildDifficultyTrend(difficultyTrends);

        return DashboardClassResponse.builder()
                .curriculum(key.curriculum())
                .classId(key.classId())
                .alertCount(alerts.size())
                .participantCount(participantCount)
                .avgConfusedScore(avgScore)
                .signalBreakdown(signalBreakdown)
                .difficultyTrend(difficultyTrend)
                .topTopics(topTopics)
                .recentAlerts(recentAlerts)
                .build();
    }

    private List<DifficultyTrendPointResponse> buildDifficultyTrend(List<UnderstandingDifficultyTrend> difficultyTrends) {
        Map<Integer, TrendAccumulator> accumulatorByHour = new HashMap<>();

        for (UnderstandingDifficultyTrend trend : difficultyTrends) {
            if (trend.getCapturedAt() == null || trend.getDifficultyScore() == null) {
                continue;
            }

            int hour = trend.getCapturedAt().getHour();
            TrendAccumulator accumulator = accumulatorByHour.computeIfAbsent(hour, ignored -> new TrendAccumulator());
            accumulator.add(trend.getDifficultyScore());
        }

        return accumulatorByHour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> DifficultyTrendPointResponse.builder()
                        .time(String.format("%02d:00", entry.getKey()))
                        .avgDifficultyScore(entry.getValue().average())
                        .sampleCount(entry.getValue().count())
                        .build())
                .toList();
    }

    private ClassGroupingKey resolveClassGroupingKey(
            String sessionId,
            String snapshotCurriculum,
            String snapshotClassId,
            Map<String, Session> sessionBySessionId,
            Map<String, SessionParticipant> participantSnapshotBySessionId
    ) {
        Session session = sessionId != null ? sessionBySessionId.get(sessionId) : null;
        SessionParticipant participant = sessionId != null ? participantSnapshotBySessionId.get(sessionId) : null;

        String curriculum = session != null && session.getCurriculum() != null
                ? session.getCurriculum()
                : (snapshotCurriculum != null ? snapshotCurriculum : (participant != null ? participant.getCurriculum() : ""));
        String classId = session != null && session.getClassId() != null
                ? session.getClassId()
                : (snapshotClassId != null ? snapshotClassId : (participant != null ? participant.getClassId() : "unknown"));

        return new ClassGroupingKey(curriculum, classId);
    }

    private String getSignalLabel(String signalType) {
        return switch (signalType) {
            case "GAZE_AWAY" -> "시선 이탈 / 화면 이탈";
            case "MANUAL_HELP" -> "학생 직접 반응";
            case "FACIAL_INSTABILITY" -> "표정 기반 불안정";
            default -> "기타 신호";
        };
    }

    private FilteredAlertContext getFilteredAlertContext(LocalDate date, String curriculum, String classId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Alert> alertsForDate = alertRepository.findByCapturedAtBetweenOrderByCapturedAtDesc(startOfDay, endOfDay);

        Set<String> sessionIds = alertsForDate.stream()
                .map(Alert::getSessionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Session> sessionBySessionId = sessionRepository.findAllBySessionIdIn(sessionIds).stream()
                .collect(Collectors.toMap(Session::getSessionId, session -> session));
        Map<String, SessionParticipant> participantSnapshotBySessionId = sessionParticipantRepository.findByJoinedAtBetween(startOfDay, endOfDay).stream()
                .filter(participant -> participant.getSessionId() != null)
                .collect(Collectors.toMap(
                        SessionParticipant::getSessionId,
                        participant -> participant,
                        (first, second) -> first
                ));

        List<Alert> filteredAlerts = alertsForDate.stream()
                .filter(alert -> matchesCurriculum(
                        sessionBySessionId.get(alert.getSessionId()),
                        alert,
                        participantSnapshotBySessionId.get(alert.getSessionId()),
                        curriculum
                ))
                .filter(alert -> matchesClassId(
                        sessionBySessionId.get(alert.getSessionId()),
                        alert,
                        participantSnapshotBySessionId.get(alert.getSessionId()),
                        classId
                ))
                .toList();

        return new FilteredAlertContext(filteredAlerts, sessionBySessionId);
    }

    private boolean matchesCurriculum(Session session, Alert alert, SessionParticipant participant, String curriculum) {
        if (curriculum == null || curriculum.isBlank()) {
            return true;
        }
        if (session != null && session.getCurriculum() != null) {
            return curriculum.equals(session.getCurriculum());
        }
        if (alert != null && curriculum.equals(alert.getCurriculum())) {
            return true;
        }
        return participant != null && curriculum.equals(participant.getCurriculum());
    }

    private boolean matchesClassId(Session session, Alert alert, SessionParticipant participant, String classId) {
        if (classId == null || classId.isBlank() || "전체 반".equals(classId)) {
            return true;
        }
        if (session != null && session.getClassId() != null) {
            return classId.equals(session.getClassId());
        }
        if (alert != null && classId.equals(alert.getClassId())) {
            return true;
        }
        return participant != null && classId.equals(participant.getClassId());
    }

    private int calculateConfusionPercent(Alert alert) {
        if (alert.getTotalStudentCount() != null && alert.getTotalStudentCount() > 0 && alert.getStudentCount() != null) {
            return (int) Math.round((alert.getStudentCount() * 100.0) / alert.getTotalStudentCount());
        }
        return (int) Math.round((alert.getConfusedScore() != null ? alert.getConfusedScore() : 0.0) * 100);
    }

    private record FilteredAlertContext(List<Alert> alerts, Map<String, Session> sessionBySessionId) {
    }

    private record ClassGroupingKey(String curriculum, String classId) {
    }

    private static class TrendAccumulator {
        private double total;
        private long count;

        void add(double value) {
            total += value;
            count += 1;
        }

        double average() {
            return count > 0 ? total / count : 0.0;
        }

        long count() {
            return count;
        }
    }
}
