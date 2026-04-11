package com.iknow.controller;

import com.iknow.dto.request.LectureChunkRequest;
import com.iknow.dto.response.AlertResponse;
import com.iknow.service.LectureChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lecture-chunk")
@RequiredArgsConstructor
public class LectureChunkController {

    private final LectureChunkService lectureChunkService;

    @PostMapping
    public ResponseEntity<AlertResponse> saveLectureChunk(@RequestBody LectureChunkRequest request) {
        return ResponseEntity.ok(lectureChunkService.saveLectureChunk(request));
    }
}
