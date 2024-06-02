package com.yangyoung.english.task.controller;

import  com.yangyoung.english.task.dto.request.*;
import com.yangyoung.english.task.dto.response.LectureTaskResponse;
import com.yangyoung.english.task.dto.response.StudentTaskResponse;
import com.yangyoung.english.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/student")
    @Operation(summary = "학생 과제 추가", description = "학생 과제를 추가합니다.")
    public ResponseEntity<StudentTaskResponse> addStudentTask(@RequestBody StudentTaskAddRequest request) {
        StudentTaskResponse response = taskService.addStudentTask(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student")
    @Operation(summary = "학생 과제 전체 조회", description = "학생 과제를 전체 조회합니다.")
    public ResponseEntity<List<StudentTaskResponse>> getAllStudentTask(@RequestParam Long studentId) {
        List<StudentTaskResponse> response = taskService.getAllStudentTask(studentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/student")
    @Operation(summary = "학생 과제 수정", description = "학생 과제를 수정합니다.")
    public ResponseEntity<Void> updateStudentTask(@RequestBody StudentTaskUpdateRequest request) {
        taskService.updateStudentTask(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/student/progress")
    @Operation(summary = "학생 과제 진행도 수정", description = "학생 과제 진행도를 수정합니다.")
    public ResponseEntity<Void> updateStudentTaskProgress(@RequestBody StudentTaskProgressUpdateRequest request) {
        taskService.updateStudentTaskProgress(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lecture")
    @Operation(summary = "강의 과제 추가", description = "강의 과제를 추가합니다.")
    public ResponseEntity<LectureTaskResponse> addLectureTask(@RequestBody LectureTaskAddRequest request) {
        LectureTaskResponse response = taskService.addLectureTask(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lecture")
    @Operation(summary = "강의 과제 전체 조회", description = "강의 과제를 전체 조회합니다.")
    public ResponseEntity<List<LectureTaskResponse>> getAllLectureTask(@RequestParam Long lectureId) {
        List<LectureTaskResponse> response = taskService.getAllLectureTask(lectureId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/lecture")
    @Operation(summary = "강의 과제 수정", description = "강의 과제를 수정합니다.")
    public ResponseEntity<Void> updateLectureTask(@RequestBody LectureTaskUpdateRequest request) {
        taskService.updateLectureTask(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/single")
    @Operation(summary = "과제 단일 삭제", description = "과제를 단일 삭제합니다.")
    public ResponseEntity<Void> deleteTask(@RequestParam Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/multiple")
    @Operation(summary = "과제 다중 삭제 ", description = "과제를 다중 삭제합니다.")
    public ResponseEntity<Void> deleteStudentTask(@RequestParam List<Long> taskIdList) {
        taskService.deleteTaskList(taskIdList);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{studentId}")
    @Operation(summary = "학생별 과제 전체 조회", description = "학생별 과제 전체 조회합니다.")
    public ResponseEntity<List<StudentTaskResponse>> getAllTaskByStudent(@PathVariable(value = "studentId") Long studentId) {

        List<StudentTaskResponse> responses = taskService.getAllTaskByStudent(studentId);

        return ResponseEntity.ok(responses);
    }
}
