package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ConfusedEventRequest {
    private String studentId;
    private String studentName;    // 수강생 이름
    private String sessionId;
    private Integer studentCount;
    private Integer totalStudentCount;
    private LocalDateTime capturedAt;
    private Double confusedScore;
    private String reason;
    private String signalType;
    private String signalSubtype;
}
