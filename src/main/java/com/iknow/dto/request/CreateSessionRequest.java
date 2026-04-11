package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSessionRequest {
    private String classId;
    private Integer thresholdPct;  // 혼란 감지 임계값 % (기본 50, 대시보드 참고용)
    private String curriculum;     // 커리큘럼 텍스트 (대시보드 참고용)
}
