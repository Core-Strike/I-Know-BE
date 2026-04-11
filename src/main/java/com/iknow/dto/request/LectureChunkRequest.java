package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LectureChunkRequest {
    private String sessionId;
    private String classId;
    private String topicText;       // STT 변환 텍스트
    private LocalDateTime capturedAt;
}
