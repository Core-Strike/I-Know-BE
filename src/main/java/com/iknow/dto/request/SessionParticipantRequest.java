package com.iknow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SessionParticipantRequest {
    private String studentId;
    private String studentName;
}
