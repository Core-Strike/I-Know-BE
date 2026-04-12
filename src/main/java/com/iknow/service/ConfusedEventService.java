package com.iknow.service;

import com.iknow.dto.request.ConfusedEventRequest;
import com.iknow.dto.response.AlertWebSocketPayload;
import com.iknow.entity.LearningSignalEvent;
import com.iknow.entity.Session;
import com.iknow.repository.LearningSignalEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ConfusedEventService {

    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LearningSignalEventRepository learningSignalEventRepository;

    @Transactional
    public void handleConfusedEvent(ConfusedEventRequest request) {
        Session session = sessionService.getActiveSessionOrThrow(request.getSessionId());

        learningSignalEventRepository.save(LearningSignalEvent.builder()
                .sessionId(request.getSessionId())
                .classId(session.getClassId())
                .curriculum(session.getCurriculum())
                .studentId(request.getStudentId())
                .studentName(request.getStudentName())
                .signalType(normalizeSignalType(request.getSignalType()))
                .signalSubtype(normalizeBlankToNull(request.getSignalSubtype()))
                .score(request.getConfusedScore())
                .capturedAt(request.getCapturedAt())
                .build());

        AlertWebSocketPayload payload = AlertWebSocketPayload.builder()
                .sessionId(request.getSessionId())
                .classId(session.getClassId())
                .studentCount(request.getStudentCount() != null ? request.getStudentCount() : 1)
                .totalStudentCount(request.getTotalStudentCount() != null ? request.getTotalStudentCount() : 1)
                .confusedScore(request.getConfusedScore())
                .reason(request.getReason())
                .capturedAt(request.getCapturedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        messagingTemplate.convertAndSend("/topic/alert/" + request.getSessionId(), payload);
    }

    private String normalizeSignalType(String signalType) {
        String normalized = normalizeBlankToNull(signalType);
        return normalized != null ? normalized : "FACIAL_INSTABILITY";
    }

    private String normalizeBlankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
