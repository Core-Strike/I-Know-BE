package com.iknow.controller;

import com.iknow.dto.request.CreateCurriculumRequest;
import com.iknow.dto.response.CurriculumResponse;
import com.iknow.service.CurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @GetMapping
    public ResponseEntity<List<CurriculumResponse>> getCurriculums() {
        return ResponseEntity.ok(curriculumService.getCurriculums());
    }

    @PostMapping
    public ResponseEntity<CurriculumResponse> createCurriculum(@RequestBody CreateCurriculumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(curriculumService.createCurriculum(request));
    }

    @DeleteMapping("/{curriculumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurriculum(@PathVariable Long curriculumId) {
        curriculumService.deleteCurriculum(curriculumId);
    }
}
