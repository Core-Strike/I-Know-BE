package com.iknow.service;

import com.iknow.dto.request.CreateSessionRequest;
import com.iknow.dto.response.SessionResponse;
import com.iknow.entity.Session;
import com.iknow.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        Session session = Session.builder()
                .sessionId(UUID.randomUUID().toString())
                .classId(request.getClassId())
                .status(Session.SessionStatus.ACTIVE)
                .build();
        return SessionResponse.from(sessionRepository.save(session));
    }

    @Transactional
    public SessionResponse endSession(String sessionId) {
        Session session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        session.setStatus(Session.SessionStatus.ENDED);
        session.setEndedAt(LocalDateTime.now());
        return SessionResponse.from(sessionRepository.save(session));
    }
}
