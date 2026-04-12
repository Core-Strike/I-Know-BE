package com.iknow.dto.response;

import com.iknow.entity.LearningSignalEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LearningSignalEventResponse {
    private Long id;
    private String sessionId;
    private String classId;
    private String curriculum;
    private String studentId;
    private String studentName;
    private String signalType;
    private String signalSubtype;
    private Double score;
    private LocalDateTime capturedAt;
    private LocalDateTime createdAt;

    public static LearningSignalEventResponse from(LearningSignalEvent event) {
        return LearningSignalEventResponse.builder()
                .id(event.getId())
                .sessionId(event.getSessionId())
                .classId(event.getClassId())
                .curriculum(event.getCurriculum())
                .studentId(event.getStudentId())
                .studentName(event.getStudentName())
                .signalType(event.getSignalType())
                .signalSubtype(event.getSignalSubtype())
                .score(event.getScore())
                .capturedAt(event.getCapturedAt())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
