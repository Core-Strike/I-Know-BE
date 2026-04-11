package com.iknow.service;

import com.iknow.dto.request.ConfusedEventRequest;
import com.iknow.dto.response.AlertWebSocketPayload;
import com.iknow.entity.Alert;
import com.iknow.entity.LectureTopic;
import com.iknow.repository.AlertRepository;
import com.iknow.repository.LectureTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfusedEventService {

    private final AlertRepository alertRepository;
    private final LectureTopicRepository lectureTopicRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void handleConfusedEvent(ConfusedEventRequest request) {

        // confused 발생 시각 기준, 가장 가까운 강의 토픽 매칭
        Optional<LectureTopic> latestTopic = lectureTopicRepository
                .findTopBySessionIdAndCapturedAtLessThanEqualOrderByCapturedAtDesc(
                        request.getSessionId(), request.getCapturedAt());

        String unclearTopic = latestTopic.map(LectureTopic::getTopicText).orElse(null);

        // Alert DB 저장
        Alert alert = Alert.builder()
                .sessionId(request.getSessionId())
                .studentId(request.getStudentId())
                .capturedAt(request.getCapturedAt())
                .confusedScore(request.getConfusedScore())
                .reason(request.getReason())
                .unclearTopic(unclearTopic)
                .build();
        alertRepository.save(alert);

        // 강사에게 WebSocket 푸시
        AlertWebSocketPayload payload = AlertWebSocketPayload.builder()
                .studentId(request.getStudentId())
                .sessionId(request.getSessionId())
                .confusedScore(request.getConfusedScore())
                .reason(request.getReason())
                .unclearTopic(unclearTopic)
                .capturedAt(request.getCapturedAt()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        messagingTemplate.convertAndSend(
                "/topic/alert/" + request.getSessionId(), payload);
    }
}
