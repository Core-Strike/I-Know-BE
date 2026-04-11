package com.iknow.repository;

import com.iknow.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);
    List<Session> findAllBySessionIdIn(Collection<String> sessionIds);
    boolean existsBySessionId(String sessionId);  // 6자리 중복 체크용
}
