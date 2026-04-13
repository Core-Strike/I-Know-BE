package com.iknow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "understanding_difficulty_trends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderstandingDifficultyTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    private String classId;

    private String curriculum;

    @Column(nullable = false)
    private Double difficultyScore;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
