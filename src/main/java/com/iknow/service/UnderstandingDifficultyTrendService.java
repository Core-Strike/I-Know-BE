package com.iknow.service;

import com.iknow.dto.request.UnderstandingDifficultyTrendRequest;
import com.iknow.dto.response.UnderstandingDifficultyTrendResponse;
import com.iknow.entity.Session;
import com.iknow.entity.UnderstandingDifficultyTrend;
import com.iknow.repository.UnderstandingDifficultyTrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UnderstandingDifficultyTrendService {

    private final UnderstandingDifficultyTrendRepository understandingDifficultyTrendRepository;
    private final SessionService sessionService;

    @Transactional
    public UnderstandingDifficultyTrendResponse saveTrend(UnderstandingDifficultyTrendRequest request) {
        Session session = sessionService.getActiveSessionOrThrow(request.getSessionId());

        double difficultyScore = request.getDifficultyScore() != null ? request.getDifficultyScore() : 0.0;
        difficultyScore = Math.max(0.0, Math.min(100.0, difficultyScore));

        UnderstandingDifficultyTrend trend = UnderstandingDifficultyTrend.builder()
                .sessionId(session.getSessionId())
                .classId(session.getClassId())
                .curriculum(session.getCurriculum())
                .difficultyScore(difficultyScore)
                .capturedAt(request.getCapturedAt() != null ? request.getCapturedAt() : LocalDateTime.now())
                .build();

        return UnderstandingDifficultyTrendResponse.from(understandingDifficultyTrendRepository.save(trend));
    }
}
