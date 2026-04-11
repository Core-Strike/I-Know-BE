package com.iknow.dto.response;

import com.iknow.entity.Session;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SessionResponse {
    private String sessionId;
    private String classId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static SessionResponse from(Session session) {
        return SessionResponse.builder()
                .sessionId(session.getSessionId())
                .classId(session.getClassId())
                .status(session.getStatus().name())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .build();
    }
}
