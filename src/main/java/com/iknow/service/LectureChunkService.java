package com.iknow.service;

import com.iknow.dto.request.LectureChunkRequest;
import com.iknow.entity.LectureTopic;
import com.iknow.repository.LectureTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LectureChunkService {

    private final LectureTopicRepository lectureTopicRepository;

    @Transactional
    public void saveLectureChunk(LectureChunkRequest request) {
        LectureTopic topic = LectureTopic.builder()
                .sessionId(request.getSessionId())
                .classId(request.getClassId())
                .topicText(request.getTopicText())
                .capturedAt(request.getCapturedAt() != null
                        ? request.getCapturedAt()
                        : LocalDateTime.now())
                .build();
        lectureTopicRepository.save(topic);
    }
}
