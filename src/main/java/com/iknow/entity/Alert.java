package com.iknow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    private String studentId;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    private Double confusedScore;

    @Column(columnDefinition = "TEXT")
    private String reason;

    // confused 발생 시점 기준 가장 가까운 강의 토픽 (LectureTopic에서 매칭)
    private String unclearTopic;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
