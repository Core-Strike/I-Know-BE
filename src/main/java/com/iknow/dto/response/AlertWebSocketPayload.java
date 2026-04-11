package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertWebSocketPayload {
    private String studentId;
    private String sessionId;
    private Double confusedScore;
    private String reason;
    private String unclearTopic;  // 해당 시점 강의 토픽 (STT 텍스트)
    private String capturedAt;
}
