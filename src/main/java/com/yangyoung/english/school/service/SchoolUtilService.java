package com.yangyoung.english.school.service;

import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.school.domain.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolUtilService {

    private final SchoolRepository schoolRepository;

    @Transactional
    public School getSchoolByName(String schoolName) {
        if (schoolName.isBlank()) {
            return null;
        }
        return schoolRepository.findByName(schoolName)
                .orElseGet(() -> schoolRepository.save(School.builder().name(schoolName).build()));
    }
}
