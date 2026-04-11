package com.iknow.controller;

import com.iknow.dto.request.ConfusedEventRequest;
import com.iknow.dto.response.AlertResponse;
import com.iknow.repository.AlertRepository;
import com.iknow.service.ConfusedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ConfusedEventController {

    private final ConfusedEventService confusedEventService;
    private final AlertRepository alertRepository;

    // POST /api/confused-events — 교육생 confused 이벤트 수신 → Alert 저장 + WebSocket 푸시
    @PostMapping("/api/confused-events")
    public ResponseEntity<Void> handleConfusedEvent(@RequestBody ConfusedEventRequest request) {
        confusedEventService.handleConfusedEvent(request);
        return ResponseEntity.ok().build();
    }

    // GET /api/sessions/:id/alerts — 세션 알림 이력
    @GetMapping("/api/sessions/{sessionId}/alerts")
    public ResponseEntity<List<AlertResponse>> getAlerts(@PathVariable String sessionId) {
        List<AlertResponse> alerts = alertRepository
                .findBySessionIdOrderByCapturedAtDesc(sessionId)
                .stream()
                .map(AlertResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    // GET /api/sessions/:id/confused-events — confused 이벤트 목록
    @GetMapping("/api/sessions/{sessionId}/confused-events")
    public ResponseEntity<List<AlertResponse>> getConfusedEvents(@PathVariable String sessionId) {
        List<AlertResponse> events = alertRepository
                .findBySessionIdOrderByCapturedAtDesc(sessionId)
                .stream()
                .map(AlertResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }
}
