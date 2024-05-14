package com.yangyoung.english.task.service;

import com.yangyoung.english.studentTask.domain.StudentTask;
import com.yangyoung.english.studentTask.domain.StudentTaskRepository;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.domain.TaskRepository;
import com.yangyoung.english.task.dto.response.TaskBriefResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskUtilService {

    private final TaskRepository taskRepository;
    private final StudentTaskRepository studentTaskRepository;

    // 학생 & 날짜별 과제 조회
    @Transactional
    public List<Task> getTaskByStudentAndDate(Long studentId, LocalDate date) {
        return studentTaskRepository.findByStudentIdAndDate(studentId, date).stream()
                .map(StudentTask::getTask)
                .toList();
    }
}
