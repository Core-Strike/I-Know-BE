package com.iknow.repository;

import com.iknow.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);

    List<Session> findAllBySessionIdIn(Collection<String> sessionIds);

    @Query("""
            select s
            from Session s
            where s.status = :status
              and s.curriculum = :curriculum
              and (
                    s.classId = :classId
                    or ((s.classId is null or s.classId = '') and :classId = '')
                  )
            """)
    List<Session> findActiveSessionsForScope(
            @Param("status") Session.SessionStatus status,
            @Param("curriculum") String curriculum,
            @Param("classId") String classId
    );

    boolean existsBySessionId(String sessionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Session s where s.sessionId = :sessionId")
    int deleteBySessionIdIfExists(@Param("sessionId") String sessionId);
}
