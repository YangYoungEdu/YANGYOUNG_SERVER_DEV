package com.yangyoung.english.lecture.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // 강의명 중복 검사
    boolean existsByName(String name);

    // 강의 조회 - 월 단위
    @Query("SELECT l FROM Lecture l JOIN l.lectureDateList ld WHERE FUNCTION('YEAR', ld.lectureDate) = :year AND FUNCTION('MONTH', ld.lectureDate) = :month")
    List<Lecture> findLecturesByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // 강의 조회 - 주 단위
    @Query("SELECT l FROM Lecture l JOIN l.lectureDateList ld WHERE FUNCTION('YEAR', ld.lectureDate) = :year AND FUNCTION('MONTH', ld.lectureDate) = :month AND FUNCTION('WEEK', ld.lectureDate) = :week")
    List<Lecture> findLecturesByYearAndMonthAndWeek(@Param("year") int year, @Param("month") int month, @Param("week") int week);

    // 강의 조회 - LocalDate 기준
    @Query("SELECT l FROM Lecture l JOIN l.lectureDateList ld WHERE ld.lectureDate = :date")
    List<Lecture> findLecturesByDate(@Param("date") LocalDate date);

    // 강의 조회 - 주 단위(첫날, 마지막날)
    List<Lecture> findByLectureDateList_LectureDateBetween(LocalDate startDate, LocalDate endDate);

    // 강의 조회 - 종료되지 않은 강의
    List<Lecture> findByIsFinishedFalse();
}
