package com.iknow.controller;

import com.iknow.dto.request.UnderstandingDifficultyTrendRequest;
import com.iknow.dto.response.UnderstandingDifficultyTrendResponse;
import com.iknow.service.UnderstandingDifficultyTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/understanding-difficulty-trends")
@RequiredArgsConstructor
public class UnderstandingDifficultyTrendController {

    private final UnderstandingDifficultyTrendService understandingDifficultyTrendService;

    @PostMapping
    public ResponseEntity<UnderstandingDifficultyTrendResponse> saveTrend(
            @RequestBody UnderstandingDifficultyTrendRequest request
    ) {
        return ResponseEntity.ok(understandingDifficultyTrendService.saveTrend(request));
    }
}
