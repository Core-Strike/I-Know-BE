package com.iknow.controller;

import com.iknow.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;

    // DELETE /api/alerts/{alertId} — 강사 PASS 버튼
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        alertRepository.deleteById(alertId);
        return ResponseEntity.noContent().build();
    }
}
