package com.iknow.service;

import com.iknow.dto.request.LectureSummaryRequest;
import com.iknow.dto.response.AlertResponse;
import com.iknow.entity.Alert;
import com.iknow.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureSummaryService {

    private final AlertRepository alertRepository;

    @Transactional
    public AlertResponse saveSummary(LectureSummaryRequest request) {
        Alert alert = alertRepository.findById(request.getAlertId())
                .orElseThrow(() -> new RuntimeException("Alert not found: " + request.getAlertId()));

        alert.setLectureSummary(request.getSummary());
        return AlertResponse.from(alertRepository.save(alert));
    }

    @Transactional(readOnly = true)
    public AlertResponse getSummary(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        return AlertResponse.from(alert);
    }
}
