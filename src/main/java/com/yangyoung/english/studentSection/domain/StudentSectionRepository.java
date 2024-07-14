package com.yangyoung.english.studentSection.domain;

import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentSectionRepository extends JpaRepository<StudentSection, Long> {

    // 특정 Section에 속해있는 학생 조회하기
    @Query("SELECT ss.student FROM StudentSection ss WHERE ss.section.id = :sectionId")
    List<Student> findStudentsBySectionId(Long sectionId);

    // 특정 학생이 속해있는 Section 조회하기
    @Query("SELECT ss.section FROM StudentSection ss WHERE ss.student.id = :studentId")
    List<Section> findSectionsByStudentId(Long studentId);
}
