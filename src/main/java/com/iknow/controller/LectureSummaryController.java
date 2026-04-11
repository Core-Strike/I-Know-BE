package com.iknow.controller;

import com.iknow.dto.request.LectureSummaryRequest;
import com.iknow.dto.response.AlertResponse;
import com.iknow.service.LectureSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LectureSummaryController {

    private final LectureSummaryService lectureSummaryService;

    @PostMapping("/api/lecture-summary")
    public ResponseEntity<AlertResponse> saveSummary(@RequestBody LectureSummaryRequest request) {
        return ResponseEntity.ok(lectureSummaryService.saveSummary(request));
    }

    @GetMapping("/api/alerts/{alertId}/summary")
    public ResponseEntity<AlertResponse> getSummary(@PathVariable Long alertId) {
        return ResponseEntity.ok(lectureSummaryService.getSummary(alertId));
    }
}
