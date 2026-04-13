package com.iknow.repository;

import com.iknow.entity.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
    Optional<SessionParticipant> findTopBySessionIdAndStudentIdOrderByJoinedAtDesc(String sessionId, String studentId);
    List<SessionParticipant> findByJoinedAtBetween(LocalDateTime start, LocalDateTime end);
    List<SessionParticipant> findBySessionIdAndActiveTrue(String sessionId);
    List<SessionParticipant> findBySessionIdAndStudentIdAndActiveTrue(String sessionId, String studentId);

    @Query("""
            select count(distinct sp.studentId)
            from SessionParticipant sp
            where sp.sessionId = :sessionId
              and sp.active = true
              and sp.studentId is not null
              and sp.studentId <> ''
            """)
    long countDistinctActiveStudentsBySessionId(@Param("sessionId") String sessionId);
}
