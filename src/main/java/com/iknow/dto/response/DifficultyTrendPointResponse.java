package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyTrendPointResponse {
    private String time;
    private double avgDifficultyScore;
    private long sampleCount;
}
