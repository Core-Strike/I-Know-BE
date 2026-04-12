package com.iknow.dto.response;

import com.iknow.entity.Curriculum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CurriculumResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public static CurriculumResponse from(Curriculum curriculum) {
        return CurriculumResponse.builder()
                .id(curriculum.getId())
                .name(curriculum.getName())
                .createdAt(curriculum.getCreatedAt())
                .build();
    }
}
