package com.yangyoung.english.lectureSection.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LectureSectionRepository extends JpaRepository<LectureSection, Long> {

    @Query("SELECT COUNT(DISTINCT l) FROM LectureSection ls " +
            "JOIN ls.lecture l " +
            "JOIN l.lectureDateList ld " +
            "WHERE ls.id = :sectionId " +
            "AND ld.lectureDate BETWEEN :startDate AND :endDate " +
            "AND l.isFinished = false ")
    Long countLecturesBySectionIdAndDateRange(@Param("sectionId") Long sectionId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    Long countBySectionIdAndLectureIsFinishedFalseAndLectureLectureDateListLectureDateBetween(Long sectionId, LocalDate startDate, LocalDate endDate);
}