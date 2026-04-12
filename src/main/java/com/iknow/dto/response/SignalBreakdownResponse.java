package com.iknow.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignalBreakdownResponse {
    private String signalType;
    private String label;
    private long count;
    private double ratio;
}
