package com.yangyoung.english.school.service;

import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.school.domain.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolUtilService {

    private final SchoolRepository schoolRepository;

    @Transactional
    public School getSchoolByName(String schoolName) {
        if (schoolName.isBlank()) {
            return null;
        }

        Optional<School> school = schoolRepository.findByName(schoolName);
        if (school.isEmpty()) {
            School newSchool = School.builder().name(schoolName).build();
            return schoolRepository.save(newSchool);
        }

        return school.get();
    }
}
