package com.yangyoung.english.section.service;

import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.section.domain.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionUtilService {

    private final SectionRepository sectionRepository;

    @Transactional
    public Section findSectionByName(String sectionName) {
        if (sectionName.isBlank()) {
            return null;
        }

        System.out.println("sectionName = " + sectionName);

        Optional<Section> section = sectionRepository.findByName(sectionName);
        if (section.isEmpty()) {
            Section newSection = Section.builder().name(sectionName).build();
            return sectionRepository.save(newSection);
        }

        return section.get();
    }
}
