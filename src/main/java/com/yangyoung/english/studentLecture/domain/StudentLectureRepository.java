package com.yangyoung.english.studentLecture.domain;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {

    void deleteByLectureId(Long lectureId);

    // 강의 ID로 수강 학생 조회
    @Query("SELECT sl.student FROM StudentLecture sl " +
            "JOIN sl.lecture l " +
            "WHERE l.id = :lectureId " +
            "AND l.isFinished = false")
    List<Student> findStudentsByLectureId(@Param("lectureId") Long lectureId);


    // 학생 ID로 수강 강의 조회
    @Query("SELECT sl.lecture FROM StudentLecture sl " +
            "JOIN sl.lecture l " +
            "WHERE sl.student.id = :studentId " +
            "AND l.isFinished = false")
    List<Lecture> findLecturesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(l) FROM StudentLecture sl " +
            "JOIN sl.lecture l " +
            "WHERE sl.student.id = :studentId " +
            "AND EXISTS (SELECT ld FROM l.lectureDateList ld " +
            "WHERE ld.lectureDate BETWEEN :startDate AND :endDate) " +
            "AND l.lectureType = 'CLASS' " +
            "AND l.isFinished = false")
    long countClassLecturesByStudentAndWeek(@Param("studentId") Long studentId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
