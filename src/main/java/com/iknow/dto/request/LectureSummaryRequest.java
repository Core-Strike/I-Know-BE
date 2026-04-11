package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureSummaryRequest {
    private Long alertId;
    private String summary;
}
