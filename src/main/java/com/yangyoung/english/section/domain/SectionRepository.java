package com.yangyoung.english.section.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByName(String name);

    List<Section> findByNameIn(List<String> names);
}
