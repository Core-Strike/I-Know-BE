package com.iknow.service;

import com.iknow.dto.request.CreateSessionRequest;
import com.iknow.dto.response.SessionResponse;
import com.iknow.entity.Session;
import com.iknow.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        String sessionId = generateUniqueSessionId();

        Session session = Session.builder()
                .sessionId(sessionId)
                .classId(request.getClassId())
                .thresholdPct(request.getThresholdPct() != null ? request.getThresholdPct() : 50)
                .curriculum(request.getCurriculum())
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

    // 100000~999999 범위 랜덤 숫자, 중복이면 재생성
    private String generateUniqueSessionId() {
        String sessionId;
        do {
            int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
            sessionId = String.valueOf(random);
        } while (sessionRepository.existsBySessionId(sessionId));
        return sessionId;
    }
}
