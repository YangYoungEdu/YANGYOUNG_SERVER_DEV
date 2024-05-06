package com.yangyoung.english.student.controller;

import com.yangyoung.english.student.dto.request.StudentAddByExcelRequest;
import com.yangyoung.english.student.dto.request.StudentRequest;
import com.yangyoung.english.student.dto.request.StudentsDischargeRequest;
import com.yangyoung.english.student.dto.request.StudentsSeqUpdateRequest;
import com.yangyoung.english.student.dto.response.StudentAddByExcelResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // 학생 정보 등록 - 폼 입력 컨트롤러
    @PostMapping("")
    public ResponseEntity<StudentResponse> addStudentByForm(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.addStudentByForm(request);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 등록 - 엑셀 파일 읽기 컨트롤러
    @PostMapping("/excel")
    public ResponseEntity<StudentAddByExcelResponse> addStudentByExcel(@ModelAttribute StudentAddByExcelRequest request) throws Exception {
        StudentAddByExcelResponse response = studentService.addStudentsByExcel(request);

        return ResponseEntity.ok(response);
    }

    // 학생 전체 조회 - 페이징 처리 컨트롤러
    @GetMapping("")
    public ResponseEntity<Page<StudentResponse>> getStudents(@RequestParam int page, @RequestParam int size) {
        Page<StudentResponse> response = studentService.getAllStudents(page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 상세 조회 컨트롤러
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable(value = "studentId") Long studentId) {
        StudentResponse response = studentService.getStudent(studentId);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 수정 컨트롤러
    @PatchMapping("")
    public ResponseEntity<StudentResponse> updateStudent(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.updateStudent(request);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 보관 - single 컨트롤러
    @PatchMapping("/hidden/{studentId}")
    public ResponseEntity<Void> saveStudent(@PathVariable(value = "studentId") Long studentId) {
        studentService.dischargeStudent(studentId);

        return ResponseEntity.ok().build();
    }

    // 숨김 학생 정보 조회 컨트롤러
    @GetMapping("/hidden")
    public ResponseEntity<Page<StudentResponse>> getHiddenStudents(@RequestParam int page, @RequestParam int size) {
        Page<StudentResponse> response = studentService.getHiddenStudents(page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 보관 - multiple 컨트롤러
    @PatchMapping("/hidden")
    public ResponseEntity<Void> saveStudents(@RequestBody StudentsDischargeRequest request) {
        studentService.dischargeStudents(request);

        return ResponseEntity.ok().build();
    }

    // 학생 정보 삭제 - single 컨트롤러
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable(value = "studentId") Long studentId) {
        studentService.deleteStudent(studentId);

        return ResponseEntity.ok().build();
    }

    // 학생 정보 삭제 - multiple 컨트롤러
    @DeleteMapping("")
    public ResponseEntity<Void> deleteStudents(@RequestBody List<Long> idList) {
        studentService.deleteStudents(idList);

        return ResponseEntity.ok().build();
    }

    // 학생 목록 순서 변경 컨트롤러
    @PatchMapping("/seq")
    public ResponseEntity<Void> updateStudentSeq(@RequestBody StudentsSeqUpdateRequest request) {
        studentService.updateStudentSequence(request);

        return ResponseEntity.ok().build();
    }
}
