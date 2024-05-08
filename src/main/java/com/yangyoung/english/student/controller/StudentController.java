package com.yangyoung.english.student.controller;

import com.yangyoung.english.student.dto.request.StudentAddByExcelRequest;
import com.yangyoung.english.student.dto.request.StudentRequest;
import com.yangyoung.english.student.dto.request.StudentsDischargeRequest;
import com.yangyoung.english.student.dto.request.StudentsSeqUpdateRequest;
import com.yangyoung.english.student.dto.response.StudentAddByExcelResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.service.StudentService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "학생 정보 등록 - 폼", notes = "학생 정보를 등록합니다.")
    public ResponseEntity<StudentResponse> addStudentByForm(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.addStudentByForm(request);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 등록 - 엑셀 파일 읽기 컨트롤러
    @PostMapping("/excel")
    @ApiOperation(value = "학생 정보 등록 - 엑셀", notes = "엑셀 파일을 읽어 학생 정보를 등록합니다.")
    public ResponseEntity<StudentAddByExcelResponse> addStudentByExcel(@ModelAttribute StudentAddByExcelRequest request) throws Exception {
        StudentAddByExcelResponse response = studentService.addStudentsByExcel(request);

        return ResponseEntity.ok(response);
    }

    // 학생 전체 조회 - 페이징 처리 컨트롤러
    @GetMapping("")
    @ApiOperation(value = "학생 전체 조회 - 페이징 처리", notes = "학생 정보를 전체 조회합니다.")
    public ResponseEntity<Page<StudentResponse>> getStudents(@RequestParam int page, @RequestParam int size) {
        Page<StudentResponse> response = studentService.getAllStudents(page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 상세 조회 컨트롤러
    @GetMapping("/{studentId}")
    @ApiOperation(value = "학생 상세 조회", notes = "학생 정보를 상세 조회합니다.")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable(value = "studentId") Long studentId) {
        StudentResponse response = studentService.getStudent(studentId);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 수정 컨트롤러
    @PatchMapping("")
    @ApiOperation(value = "학생 정보 수정", notes = "학생 정보를 수정합니다.")
    public ResponseEntity<StudentResponse> updateStudent(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.updateStudent(request);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 보관 - single 컨트롤러
    @PatchMapping("/hidden/{studentId}")
    @ApiOperation(value = "학생 숨김 처리 - single", notes = "학생 정보를 보관합니다.")
    public ResponseEntity<Void> saveStudent(@PathVariable(value = "studentId") Long studentId) {
        studentService.dischargeStudent(studentId);

        return ResponseEntity.ok().build();
    }

    // 숨김 학생 정보 조회 컨트롤러
    @GetMapping("/hidden")
    @ApiOperation(value = "숨김 학생 정보 조회", notes = "숨김 학생 정보를 조회합니다.")
    public ResponseEntity<Page<StudentResponse>> getHiddenStudents(@RequestParam int page, @RequestParam int size) {
        Page<StudentResponse> response = studentService.getHiddenStudents(page, size);

        return ResponseEntity.ok(response);
    }

    // 학생 정보 보관 - multiple 컨트롤러
    @PatchMapping("/hidden")
    @ApiOperation(value = "학생 숨김 처리 - multiple", notes = "학생 정보를 보관합니다.")
    public ResponseEntity<Void> saveStudents(@RequestBody StudentsDischargeRequest request) {
        studentService.dischargeStudents(request);

        return ResponseEntity.ok().build();
    }

    // 학생 정보 삭제 - single 컨트롤러
    @DeleteMapping("/{studentId}")
    @ApiOperation(value = "학생 정보 삭제 - single", notes = "학생 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteStudent(@PathVariable(value = "studentId") Long studentId) {
        studentService.deleteStudent(studentId);

        return ResponseEntity.ok().build();
    }

    // 학생 정보 삭제 - multiple 컨트롤러
    @DeleteMapping("")
    @ApiOperation(value = "학생 정보 삭제 - multiple", notes = "학생 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteStudents(@RequestParam List<Long> idList) {
        studentService.deleteStudents(idList);

        return ResponseEntity.ok().build();
    }

    // 학생 목록 순서 변경 컨트롤러
    @PatchMapping("/seq")
    @ApiOperation(value = "학생 목록 순서 변경", notes = "학생 목록의 순서를 변경합니다.")
    public ResponseEntity<Void> updateStudentSeq(@RequestBody StudentsSeqUpdateRequest request) {
        studentService.updateStudentSequence(request);

        return ResponseEntity.ok().build();
    }
}
