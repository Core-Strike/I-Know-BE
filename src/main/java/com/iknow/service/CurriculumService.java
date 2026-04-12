package com.iknow.service;

import com.iknow.dto.request.CreateCurriculumRequest;
import com.iknow.dto.response.CurriculumResponse;
import com.iknow.entity.Curriculum;
import com.iknow.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;

    @Transactional(readOnly = true)
    public List<CurriculumResponse> getCurriculums() {
        return curriculumRepository.findAllByOrderByNameAsc().stream()
                .map(CurriculumResponse::from)
                .toList();
    }

    @Transactional
    public CurriculumResponse createCurriculum(CreateCurriculumRequest request) {
        String name = request.getName() == null ? "" : request.getName().trim();
        if (name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curriculum name is required");
        }
        if (curriculumRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Curriculum already exists: " + name);
        }

        Curriculum saved = curriculumRepository.save(Curriculum.builder()
                .name(name)
                .build());
        return CurriculumResponse.from(saved);
    }

    @Transactional
    public void deleteCurriculum(Long curriculumId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curriculum not found: " + curriculumId));
        curriculumRepository.delete(curriculum);
    }
}
