package com.yangyoung.english.lecture.domain;

import com.yangyoung.english.student.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // 강의명 중복 검사
    boolean existsByName(String name);

    // 강의코드 중복 검사
    boolean existsByLectureCode(String code);

    // 강의코드로 강의 조회
    Optional<Lecture> findByLectureCode(String code);

    // 강의 조회 - 월 단위(Date)
    @Query("SELECT l FROM Lecture l " +
            "JOIN l.lectureDateList ld " +
            "WHERE FUNCTION('YEAR', ld.lectureDate) = :year " +
            "AND FUNCTION('MONTH', ld.lectureDate) = :month " +
            "AND l.isFinished = false")
    List<Lecture> findLecturesByYearAndMonth(@Param("year") int year, @Param("month") int month);


    // 강의 조회 - LocalDate 기준
    @Query("SELECT l FROM Lecture l " +
            "JOIN l.lectureDateList ld " +
            "WHERE ld.lectureDate = :date " +
            "AND l.isFinished = false")
    List<Lecture> findLecturesByDate(@Param("date") LocalDate date);


    // 강의 조회 - 종료되지 않은 강의
    List<Lecture> findByIsFinishedFalse();

    @Query("SELECT l FROM Lecture l " +
            "JOIN l.lectureDateList ld " +
            "WHERE ld.lectureDate BETWEEN :startDate AND :endDate " +
            "AND l.isFinished = false")
    List<Lecture> findLecturesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
