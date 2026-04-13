package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UnderstandingDifficultyTrendRequest {
    private String sessionId;
    private Double difficultyScore;
    private LocalDateTime capturedAt;
}
