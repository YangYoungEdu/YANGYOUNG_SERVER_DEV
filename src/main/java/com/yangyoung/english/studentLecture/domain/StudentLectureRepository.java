package com.yangyoung.english.studentLecture.domain;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {

    // 강의 ID로 수강 학생 조회
    @Query("SELECT sl.student FROM StudentLecture sl WHERE sl.lecture.id = :lectureId")
    List<Student> findStudentsByLectureId(Long lectureId);

    // 학생 ID로 수강 강의 조회
    @Query("SELECT sl.lecture FROM StudentLecture sl WHERE sl.student.id = :studentId")
    List<Lecture> findLecturesByStudentId(Long studentId);

    void deleteByLectureId(Long lectureId);

}
