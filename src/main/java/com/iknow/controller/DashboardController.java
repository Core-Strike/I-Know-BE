package com.iknow.controller;

import com.iknow.dto.response.DashboardClassResponse;
import com.iknow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard/classes — 반별 통계 (대시보드용)
    @GetMapping("/classes")
    public ResponseEntity<List<DashboardClassResponse>> getDashboardClasses() {
        return ResponseEntity.ok(dashboardService.getDashboardClasses());
    }
}
