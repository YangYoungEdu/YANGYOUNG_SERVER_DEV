package com.yangyoung.english.lectureDate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LectureDateRepository extends JpaRepository<LectureDate, Long> {

    void deleteByLectureId(Long lectureId);

    // 강의 날짜로 강의 리스트 조회
    @Query("SELECT ld FROM LectureDate ld WHERE ld.lectureDate = :date")
    List<LectureDate> findByDate(LocalDate date);
}
