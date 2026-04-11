package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LectureChunkRequest {
    private String sessionId;
    private String classId;
    private Integer studentCount;
    private Integer totalStudentCount;
    private LocalDateTime capturedAt;
    private String audioText;
    private Double confusedScore;
    private String reason;
}
