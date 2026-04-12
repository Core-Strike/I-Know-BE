package com.iknow.repository;

import com.iknow.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findAllByOrderByNameAsc();
    Optional<Curriculum> findByName(String name);
    boolean existsByName(String name);
}
