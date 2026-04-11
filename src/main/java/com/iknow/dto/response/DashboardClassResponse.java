package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardClassResponse {
    private String classId;
    private long alertCount;
    private double avgConfusedScore;
    private List<String> topTopics;       // 자주 언급된 모르는 내용 (빈도 상위 5개)
    private List<AlertResponse> recentAlerts;  // 최근 알림 10개
}
