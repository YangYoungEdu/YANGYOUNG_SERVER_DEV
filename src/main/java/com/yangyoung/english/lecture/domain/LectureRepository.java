package com.yangyoung.english.lecture.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // 강의명 중복 검사
    boolean existsByName(String name);

    // 해당 년도, 월의 강의 조회
    @Query("SELECT l FROM Lecture l JOIN l.lectureDateList ld WHERE FUNCTION('YEAR', ld.lectureDate) = :year AND FUNCTION('MONTH', ld.lectureDate) = :month")
    List<Lecture> findLecturesByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // 해당 년도, 월, 주의 강의 조회
    @Query("SELECT l FROM Lecture l JOIN l.lectureDateList ld WHERE FUNCTION('YEAR', ld.lectureDate) = :year AND FUNCTION('MONTH', ld.lectureDate) = :month AND FUNCTION('WEEK', ld.lectureDate) = :week")
    List<Lecture> findLecturesByYearAndMonthAndWeek(@Param("year") int year, @Param("month") int month, @Param("week") int week);
}
