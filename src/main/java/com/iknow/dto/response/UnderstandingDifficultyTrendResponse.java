package com.iknow.dto.response;

import com.iknow.entity.UnderstandingDifficultyTrend;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UnderstandingDifficultyTrendResponse {
    private Long id;
    private String sessionId;
    private String classId;
    private String curriculum;
    private double difficultyScore;
    private LocalDateTime capturedAt;
    private LocalDateTime createdAt;

    public static UnderstandingDifficultyTrendResponse from(UnderstandingDifficultyTrend trend) {
        return UnderstandingDifficultyTrendResponse.builder()
                .id(trend.getId())
                .sessionId(trend.getSessionId())
                .classId(trend.getClassId())
                .curriculum(trend.getCurriculum())
                .difficultyScore(trend.getDifficultyScore() != null ? trend.getDifficultyScore() : 0.0)
                .capturedAt(trend.getCapturedAt())
                .createdAt(trend.getCreatedAt())
                .build();
    }
}
