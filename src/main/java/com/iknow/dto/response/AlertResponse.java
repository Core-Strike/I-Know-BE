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
    private String studentName;      // 수강생 이름
    private LocalDateTime capturedAt;
    private Double confusedScore;
    private String reason;
    private String unclearTopic;
    private String lectureText;      // 이벤트 직후 2분 녹음 STT 원문
    private String lectureSummary;   // GPT 요약문
    private LocalDateTime createdAt;

    public static AlertResponse from(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .sessionId(alert.getSessionId())
                .studentId(alert.getStudentId())
                .studentName(alert.getStudentName())
                .capturedAt(alert.getCapturedAt())
                .confusedScore(alert.getConfusedScore())
                .reason(alert.getReason())
                .unclearTopic(alert.getUnclearTopic())
                .lectureText(alert.getLectureText())
                .lectureSummary(alert.getLectureSummary())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
