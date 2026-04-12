package com.iknow.service;

import com.iknow.dto.request.LectureSummaryRequest;
import com.iknow.dto.response.AlertResponse;
import com.iknow.entity.Alert;
import com.iknow.entity.AlertKeyword;
import com.iknow.repository.AlertKeywordRepository;
import com.iknow.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureSummaryService {

    private final AlertRepository alertRepository;
    private final AlertKeywordRepository alertKeywordRepository;

    @Transactional
    public AlertResponse saveSummary(LectureSummaryRequest request) {
        Alert alert = alertRepository.findById(request.getAlertId())
                .orElseThrow(() -> new RuntimeException("Alert not found: " + request.getAlertId()));

        alert.setLectureSummary(request.getSummary());
        if (request.getRecommendedConcept() != null) {
            alert.setReason(request.getRecommendedConcept());
        }

        Alert savedAlert = alertRepository.save(alert);

        alertKeywordRepository.deleteByAlertId(savedAlert.getId());
        List<String> normalizedKeywords = normalizeKeywords(request.getKeywords());
        if (!normalizedKeywords.isEmpty()) {
            alertKeywordRepository.saveAll(
                    normalizedKeywords.stream()
                            .map(keyword -> AlertKeyword.builder()
                                    .alertId(savedAlert.getId())
                                    .keyword(keyword)
                                    .build())
                            .toList()
            );
        }

        return AlertResponse.from(savedAlert, normalizedKeywords);
    }

    @Transactional(readOnly = true)
    public AlertResponse getSummary(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        List<String> keywords = alertKeywordRepository.findByAlertIdOrderByIdAsc(alertId).stream()
                .map(AlertKeyword::getKeyword)
                .toList();

        return AlertResponse.from(alert, keywords);
    }

    private List<String> normalizeKeywords(List<String> keywords) {
        if (keywords == null) {
            return List.of();
        }

        return keywords.stream()
                .filter(keyword -> keyword != null && !keyword.isBlank())
                .map(keyword -> String.join(" ", keyword.trim().split("\\s+")))
                .map(keyword -> {
                    String[] words = keyword.split(" ");
                    return words.length <= 3 ? keyword : String.join(" ", words[0], words[1], words[2]);
                })
                .distinct()
                .limit(3)
                .toList();
    }
}
