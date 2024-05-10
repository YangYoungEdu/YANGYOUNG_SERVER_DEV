package com.yangyoung.english.task.service;

import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentTask.domain.StudentTask;
import com.yangyoung.english.studentTask.domain.StudentTaskRepository;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.domain.TaskRepository;
import com.yangyoung.english.task.dto.request.StudentTaskAddRequest;
import com.yangyoung.english.task.dto.response.StudentTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final StudentTaskRepository studentTaskRepository;

    private final StudentUtilService studentUtilService;

    // 개인 과제 추가
    public StudentTaskResponse addStudentTask(StudentTaskAddRequest request) {

        Task newTask = request.toEntity();
        taskRepository.save(newTask);

        Student student = studentUtilService.findStudentById(request.getStudentId());
        StudentTask studentTask = new StudentTask(student, newTask);
        studentTaskRepository.save(studentTask);

        return new StudentTaskResponse(student, newTask);
    }

    // 개인 과제 조회
    public StudentTaskResponse getStudentTask(Long studentId, Long taskId) {

        Student student = studentUtilService.findStudentById(studentId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("해당 과제가 존재하지 않습니다."));

        return new StudentTaskResponse(student, task);
    }
}
