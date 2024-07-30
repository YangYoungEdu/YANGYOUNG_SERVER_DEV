package com.yangyoung.english.lectureDate.domain;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LectureDateRepository extends JpaRepository<LectureDate, Long> {

    void deleteByLectureId(Long lectureId);

    // 강의 날짜로 강의 리스트 조회
    @Query("SELECT ld FROM LectureDate ld WHERE ld.lectureDate = :date")
    List<LectureDate> findByDate(LocalDate date);

    // 강의 조회 - 월 단위(Month)
    @Query("SELECT ld FROM LectureDate ld WHERE FUNCTION('YEAR', ld.lectureDate) = :year AND FUNCTION('MONTH', ld.lectureDate) = :month")
    List<LectureDate> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // 강의 조회 - 특정 기간 동안
    @Query("SELECT ld FROM LectureDate ld WHERE ld.lectureDate BETWEEN :startDate AND :endDate")
    List<LectureDate> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 강의 조회 - 특정 일, 학생 아이디
    @Query("SELECT ld FROM LectureDate ld " +
            "JOIN ld.lecture l " +
            "JOIN l.studentLectureList sl " +
            "WHERE sl.student.id = :studentId " +
            "AND ld.lectureDate = :date")
    List<LectureDate> findByStudentIdAndDate(@Param("studentId") Long studentId, @Param("date") LocalDate date);

    @Modifying
    @Query("DELETE FROM LectureDate ld WHERE ld IN :lectureDateList")
    void deleteAllBy(@Param("lectureDateList") List<LectureDate> lectureDateList);
}
