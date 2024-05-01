package com.yangyoung.english.lecture.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    boolean existsByName(String name);
}
