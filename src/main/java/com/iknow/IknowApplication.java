package com.iknow;

import com.iknow.entity.Curriculum;
import com.iknow.repository.CurriculumRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@SpringBootApplication
public class IknowApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(IknowApplication.class, args);
    }

    @Bean
    CommandLineRunner seedCurriculums(CurriculumRepository curriculumRepository) {
        return args -> {
            if (curriculumRepository.count() == 0) {
                curriculumRepository.save(Curriculum.builder().name("자격증반").build());
                curriculumRepository.save(Curriculum.builder().name("웹개발반").build());
            }
        };
    }
}
