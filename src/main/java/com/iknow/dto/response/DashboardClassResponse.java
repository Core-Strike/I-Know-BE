package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardClassResponse {
    private String curriculum;
    private String classId;
    private long alertCount;
    private long participantCount;
    private double avgConfusedScore;
    private List<SignalBreakdownResponse> signalBreakdown;
    private List<DifficultyTrendPointResponse> difficultyTrend;
    private List<String> topTopics;
    private List<AlertResponse> recentAlerts;
}
