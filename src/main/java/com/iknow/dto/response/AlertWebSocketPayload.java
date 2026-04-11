package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertWebSocketPayload {
    private String sessionId;
    private String classId;
    private Double confusedScore;
    private String reason;
    private String capturedAt;
}
