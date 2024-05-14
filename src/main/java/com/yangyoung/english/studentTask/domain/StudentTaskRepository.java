package com.yangyoung.english.studentTask.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {

    // 학생과 과제로 학생 과제 조회
    List<StudentTask> findByStudentId(Long studentId);

    Optional<StudentTask> findByStudentIdAndTaskId(Long studentId, Long taskId);

    // 학생 & 날짜별 과제 조회 - task 엔티티 리턴
    @Query("select st.task from StudentTask st where st.student.id = :studentId and st.task.taskDate = :date")
    List<StudentTask> findByStudentIdAndDate(Long studentId, LocalDate date);
}
