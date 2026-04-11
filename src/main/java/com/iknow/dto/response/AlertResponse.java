package com.iknow.dto.response;

import com.iknow.entity.Alert;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlertResponse {
    private Long id;
    private String sessionId;
    private String studentId;
    private LocalDateTime capturedAt;
    private Double confusedScore;
    private String reason;
    private String unclearTopic;
    private LocalDateTime createdAt;

    public static AlertResponse from(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .sessionId(alert.getSessionId())
                .studentId(alert.getStudentId())
                .capturedAt(alert.getCapturedAt())
                .confusedScore(alert.getConfusedScore())
                .reason(alert.getReason())
                .unclearTopic(alert.getUnclearTopic())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
