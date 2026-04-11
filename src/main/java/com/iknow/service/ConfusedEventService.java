package com.iknow.service;

import com.iknow.dto.request.ConfusedEventRequest;
import com.iknow.dto.response.AlertWebSocketPayload;
import com.iknow.entity.Session;
import com.iknow.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ConfusedEventService {

    private final SessionRepository sessionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public void handleConfusedEvent(ConfusedEventRequest request) {
        Session session = sessionRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found: " + request.getSessionId()));

        AlertWebSocketPayload payload = AlertWebSocketPayload.builder()
                .sessionId(request.getSessionId())
                .classId(session.getClassId())
                .confusedScore(request.getConfusedScore())
                .reason(request.getReason())
                .capturedAt(request.getCapturedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        messagingTemplate.convertAndSend("/topic/alert/" + request.getSessionId(), payload);
    }
}
