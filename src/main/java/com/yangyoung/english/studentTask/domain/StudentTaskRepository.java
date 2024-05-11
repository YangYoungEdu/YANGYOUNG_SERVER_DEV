package com.yangyoung.english.studentTask.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {

    // 학생과 과제로 학생 과제 조회
    List<StudentTask> findByStudentId(Long studentId);

    Optional<StudentTask> findByStudentIdAndTaskId(Long studentId, Long taskId);

}
