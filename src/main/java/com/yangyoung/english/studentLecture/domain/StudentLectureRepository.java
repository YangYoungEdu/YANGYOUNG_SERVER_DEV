package com.yangyoung.english.studentLecture.domain;

import com.yangyoung.english.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {

    @Query("SELECT sl.student FROM StudentLecture sl WHERE sl.lecture.id = :lectureId")
    List<Student> findStudentsByLectureId(Long lectureId);

    void deleteByLectureId(Long lectureId);
}
