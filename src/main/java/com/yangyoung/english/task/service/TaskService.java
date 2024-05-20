package com.yangyoung.english.task.service;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.service.LectureUtilService;
import com.yangyoung.english.lectureTask.domain.LectureTask;
import com.yangyoung.english.lectureTask.domain.LectureTaskRepository;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentTask.domain.StudentTask;
import com.yangyoung.english.studentTask.domain.StudentTaskRepository;
import com.yangyoung.english.studentTask.domain.TaskProgress;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.domain.TaskRepository;
import com.yangyoung.english.task.dto.request.*;
import com.yangyoung.english.task.dto.response.LectureTaskResponse;
import com.yangyoung.english.task.dto.response.StudentTaskResponse;
import com.yangyoung.english.task.exception.TaskErrorCode;
import com.yangyoung.english.task.exception.TaskNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final LectureTaskRepository lectureTaskRepository;

    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;

    // 학생 과제 추가
    @Transactional
    public StudentTaskResponse addStudentTask(StudentTaskAddRequest request) {

        Task newTask = request.toEntity();
        taskRepository.save(newTask);

        Student student = studentUtilService.findStudentById(request.getStudentId());
        StudentTask studentTask = new StudentTask(student, newTask);
        studentTaskRepository.save(studentTask);

        return new StudentTaskResponse(student, newTask);
    }

    // 학생 과제 전체 조회
    @Transactional
    public List<StudentTaskResponse> getAllStudentTask(Long studentId) {

        List<StudentTask> studentTaskList = studentTaskRepository.findByStudentId(studentId);

        return studentTaskList.stream()
                .map(StudentTaskResponse::new)
                .toList();
    }

    // 학생 과제 정보 수정
    @Transactional
    public void updateStudentTask(StudentTaskUpdateRequest request) {

        Optional<Task> task = taskRepository.findById(request.getTaskId());
        if (task.isEmpty()) {
            TaskErrorCode taskErrorCode = TaskErrorCode.TASK_NOT_FOUND;
            throw new TaskNotFoundException(taskErrorCode, request.getTaskId());
        }

        task.get().update(request.getContent(), request.getTaskDate());
    }

    // 과제 상태 변경
    @Transactional
    public void updateStudentTaskProgress(StudentTaskProgressUpdateRequest request) {

        StudentTask studentTask = studentTaskRepository.findByStudentIdAndTaskId(request.getStudentId(), request.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("해당 학생과 과제가 존재하지 않습니다."));
        studentTask.updateTaskProgress(TaskProgress.valueOf(request.getTaskProgress()));
    }

    // 수업 과제 추가
    @Transactional
    public LectureTaskResponse addLectureTask(LectureTaskAddRequest request) {

        Task newTask = request.toEntity();
        taskRepository.save(newTask);

        Lecture lecture = lectureUtilService.findLectureById(request.getLectureId());
        LectureTask newLectureTask = new LectureTask(lecture, newTask);
        lectureTaskRepository.save(newLectureTask);

        List<Student> studentList = studentUtilService.findStudentsByLectureId(request.getLectureId());
        List<StudentTask> studentTaskList = studentList.stream()
                .map(student -> new StudentTask(student, newTask))
                .toList();
        studentTaskRepository.saveAll(studentTaskList);

        return new LectureTaskResponse(newTask);
    }

    // 수업 과제 전체 조회
    @Transactional
    public List<LectureTaskResponse> getAllLectureTask(Long lectureId) {

        List<Task> taskList = lectureTaskRepository.findByLectureId(lectureId);

        return taskList.stream()
                .map(LectureTaskResponse::new)
                .toList();
    }

    // 수업 과제 수정
    @Transactional
    public void updateLectureTask(LectureTaskUpdateRequest request) {

        Optional<Task> task = taskRepository.findById(request.getTaskId());
        if (task.isEmpty()) {
            TaskErrorCode taskErrorCode = TaskErrorCode.TASK_NOT_FOUND;
            throw new TaskNotFoundException(taskErrorCode, request.getTaskId());
        }

        task.get().update(request.getContent(), request.getTaskDate());
    }

    // 과제 삭제 - single
    @Transactional
    public void deleteTask(Long taskId) {

        boolean isTaskExist = taskRepository.existsById(taskId);
        if (!isTaskExist) {
            TaskErrorCode taskErrorCode = TaskErrorCode.TASK_NOT_FOUND;
            throw new TaskNotFoundException(taskErrorCode, taskId);
        }

        taskRepository.deleteById(taskId);
    }

    // 과제 삭제 - multiple
    @Transactional
    public void deleteTaskList(List<Long> taskIdList) {

        for (Long taskId : taskIdList) {
            boolean isTaskExist = taskRepository.existsById(taskId);
            if (!isTaskExist) {
                TaskErrorCode taskErrorCode = TaskErrorCode.TASK_NOT_FOUND;
                throw new TaskNotFoundException(taskErrorCode, taskId);
            }
        }

        taskRepository.deleteAllById(taskIdList);
    }

    // 학생별 과제 전체 조회
    @Transactional
    public List<StudentTaskResponse> getAllTaskByStudent(Long studentId) {

        List<StudentTask> taskList = studentTaskRepository.findByStudentId(studentId);

        return taskList.stream()
                .map(StudentTaskResponse::new)
                .toList();
    }
}
