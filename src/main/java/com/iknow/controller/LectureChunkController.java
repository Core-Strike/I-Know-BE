package com.iknow.controller;

import com.iknow.dto.request.LectureChunkRequest;
import com.iknow.service.LectureChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lecture-chunk")
@RequiredArgsConstructor
public class LectureChunkController {

    private final LectureChunkService lectureChunkService;

    // POST /api/lecture-chunk — 강사 STT 변환 텍스트 저장
    @PostMapping
    public ResponseEntity<Void> saveLectureChunk(@RequestBody LectureChunkRequest request) {
        lectureChunkService.saveLectureChunk(request);
        return ResponseEntity.ok().build();
    }
}
