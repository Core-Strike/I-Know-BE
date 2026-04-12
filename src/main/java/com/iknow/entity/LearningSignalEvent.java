package com.iknow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "learning_signal_events",
        indexes = {
                @Index(name = "idx_learning_signal_session", columnList = "sessionId"),
                @Index(name = "idx_learning_signal_captured_at", columnList = "capturedAt"),
                @Index(name = "idx_learning_signal_class_curriculum", columnList = "classId, curriculum")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningSignalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    private String classId;

    private String curriculum;

    private String studentId;

    private String studentName;

    @Column(nullable = false)
    private String signalType;

    private String signalSubtype;

    private Double score;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
