package com.yangyoung.english.student.controller;

import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.dto.request.*;
import com.yangyoung.english.student.dto.response.StudentBriefResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.dto.response.StudentScheduleResponse;
import com.yangyoung.english.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // 학생 등록 - 폼 입력 컨트롤러
    @PostMapping("/form")
    @Operation(summary = "학생 등록 - 폼", description = "폼의 정보를 통해 학생 정보를 등록합니다.")
    public ResponseEntity<StudentResponse> addStudentByForm(@RequestBody StudentAddRequest request,
                                                            @RequestHeader(value = "Authorization") String token) {
        StudentResponse response = studentService.addStudentByForm(request);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 등록 - 스프레드시트 읽기 컨트롤러
    @PostMapping("/spreadsheet")
    @Operation(summary = "학생 정보 등록 - 스프레드시트", description = "스프레드시트를 읽어 학생 정보를 등록합니다.")
    public ResponseEntity<Void> addStudentByExcel(@RequestHeader(value = "Authorization") String token) throws Exception {
        studentService.addStudentsBySheet();

        return ResponseEntity.ok().build();
    }

    // 학생 전체 조회 - 페이징 처리 컨트롤러
    @GetMapping("")
    @Operation(summary = "학생 전체 조회 - 페이징 처리", description = "학생 정보를 전체 조회합니다.")
    public ResponseEntity<Page<StudentResponse>> getStudents(@RequestParam int page, @RequestParam int size, @RequestHeader(value = "Authorization") String token) {
        Page<StudentResponse> response = studentService.getAllStudents(page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 상세 조회 컨트롤러
    @GetMapping("/{studentId}")
    @Operation(summary = "학생 상세 조회", description = "학생 정보를 상세 조회합니다.")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable(value = "studentId") Long studentId, @RequestHeader(value = "Authorization") String token) {
        StudentResponse response = studentService.getStudent(studentId);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 수정 컨트롤러
    @PatchMapping("")
    @Operation(summary = "학생 정보 수정", description = "학생 정보를 수정합니다.")
    public ResponseEntity<StudentResponse> updateStudent(@RequestBody StudentAddRequest request, @RequestHeader(value = "Authorization") String token) {
        StudentResponse response = studentService.updateStudent(request);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 삭제 - multiple 컨트롤러
    @DeleteMapping("")
    @Operation(summary = "학생 삭제", description = "학생 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteStudents(@RequestParam List<Long> idList, @RequestHeader(value = "Authorization") String token) {
        studentService.deleteStudents(idList);

        return ResponseEntity.ok().build();
    }

    // 학생 퇴원 처리 - multiple 컨트롤러
    @PatchMapping("/hidden")
    @Operation(summary = "학생 퇴원 처리", description = "학생 정보를 보관합니다.")
    public ResponseEntity<Void> saveStudents(@RequestBody StudentsDischargeRequest request, @RequestHeader(value = "Authorization") String token) {
        studentService.dischargeStudents(request);

        return ResponseEntity.ok().build();
    }

    // 퇴원 학생 정보 조회 컨트롤러
    @GetMapping("/hidden")
    @Operation(summary = "퇴원 학생 전체 조회 - 페이징 처리", description = "숨김 학생 정보를 전체 조회합니다.")
    public ResponseEntity<Page<StudentResponse>> getHiddenStudents(@RequestParam int page, @RequestParam int size, @RequestHeader(value = "Authorization") String token) {
        Page<StudentResponse> response = studentService.getHiddenStudents(page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 복원 - multiple 컨트롤러
    @PatchMapping("/restore")
    @Operation(summary = "퇴원 학생 복원", description = "학생 정보를 복원합니다.")
    public ResponseEntity<Void> restoreStudents(@RequestBody StudentsDischargeRequest request, @RequestHeader(value = "Authorization") String token) {
        studentService.restoreStudents(request);

        return ResponseEntity.ok().build();
    }

    // 학생 검색(이름, 학교, 학년) 컨트롤러
    @GetMapping("/search")
    @Operation(summary = "학생 검색", description = "학생 정보를 검색합니다.")
    public ResponseEntity<Page<StudentResponse>> searchStudents(
            @RequestParam(required = false) List<String> nameList,
            @RequestParam(required = false) List<String> schoolList,
            @RequestParam(required = false) List<String> gradeList,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Authorization") String token) {
        Page<StudentResponse> response = studentService.searchStudents(nameList, schoolList, gradeList, page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 날짜별 스케줄 조회 컨트롤러
    @GetMapping("/schedule")
    @Operation(summary = "학생 날짜별 스케줄 조회", description = "학생의 날짜별 스케줄을 조회합니다.")
    public ResponseEntity<StudentScheduleResponse> getStudentSchedule(@RequestParam Long studentId,
                                                                      @RequestParam LocalDate date,
                                                                      @RequestHeader(value = "Authorization") String token) {
        System.out.println("studentId = " + studentId);
        System.out.println("date = " + date);
        StudentScheduleResponse response = studentService.getStudentSchedule(studentId, date);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    @Operation(summary = "학생 당일 스케줄 조회 - client", description = "오늘 출석한 학생을 조회합니다.")
    public ResponseEntity<StudentScheduleResponse> getTodayStudents(@RequestParam Long studentId) {

        LocalDate today = LocalDate.now();
        StudentScheduleResponse responses = studentService.getStudentSchedule(studentId, today);

        return ResponseEntity.ok(responses);
    }

    // 강의별 학생 전체 조회 컨트롤러
    @GetMapping("/lecture/{lectureId}")
    @Operation(summary = "강의별 학생 전체 조회", description = "강의별 수강 학생을 조회합니다.")
    public ResponseEntity<List<StudentBriefResponse>> getStudentsByLecture(@PathVariable(value = "lectureId") Long lectureId, @RequestHeader(value = "Authorization") String token) {
        List<StudentBriefResponse> responses = studentService.getStudentsByLecture(lectureId);

        return ResponseEntity.ok(responses);
    }

    // 수업 미등록 학생 조회 컨트롤러
    @GetMapping("/unregistered/refresh")
    @Operation(summary = "미등록 학생 업데이트", description = "미등록 학생을 업데이트.")
    public ResponseEntity<Void> checkUnregisteredStudents() {
        studentService.checkUnregisteredStudents();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unregistered")
    @Operation(summary = "미등록 학생 조회", description = "미등록 학생을 조회합니다.")
    public ResponseEntity<List<StudentResponse>> getUnregisteredStudents() {
        List<StudentResponse> responses = studentService.getUnregisteredStudents();

        return ResponseEntity.ok().body(responses);
    }
}
