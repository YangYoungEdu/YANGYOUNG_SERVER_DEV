package com.yangyoung.english.section.service;

import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.section.domain.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionUtilService {

    private final SectionRepository sectionRepository;

    @Transactional
    public Section findSectionByName(String sectionName) {
        if (sectionName.isBlank()) {
            return null;
        }

        return sectionRepository.findByName(sectionName)
                .orElseGet(() -> sectionRepository.save(Section.builder().name(sectionName).build()));
    }
}
