package com.iknow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_topics")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    private String classId;

    // 강사 음성 STT 변환 텍스트 (강의 내용)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String topicText;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
