package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LectureSummaryRequest {
    private Long alertId;
    private String summary;
    private String recommendedConcept;
    private List<String> keywords;
}
