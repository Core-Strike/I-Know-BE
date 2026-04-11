package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertWebSocketPayload {
    private String sessionId;
    private String classId;
    private Integer studentCount;
    private Integer totalStudentCount;
    private Double confusedScore;
    private String reason;
    private String capturedAt;
}
