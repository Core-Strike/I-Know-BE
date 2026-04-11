package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ConfusedEventRequest {
    private String studentId;
    private String sessionId;
    private LocalDateTime capturedAt;
    private Double confusedScore;
    private String reason;
}
