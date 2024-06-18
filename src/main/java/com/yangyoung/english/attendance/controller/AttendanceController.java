package com.yangyoung.english.attendance.controller;

import com.yangyoung.english.attendance.dto.request.AttendanceUpdateRequest;
import com.yangyoung.english.attendance.dto.response.AttendanceResponse;
import com.yangyoung.english.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출석 - 학생
    @PostMapping("/attend/{studentId}")
    @Operation(summary = "출석 - 학생", description = "학생의 출석을 처리합니다.")
    public ResponseEntity<AttendanceResponse> attend(@PathVariable(value = "studentId") Long studentId) {

        AttendanceResponse response = attendanceService.attend(studentId);

        return ResponseEntity.ok(response);
    }

    // 강의별 출석 현황 조회
    @GetMapping("/lecture")
    @Operation(summary = "강의별 출석 현황 조회", description = "강의별 출석 현황을 조회합니다.")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByLecture(@RequestParam(value = "lectureId") Long lectureId,
                                                                           @RequestParam(value = "date") LocalDate date) {

        List<AttendanceResponse> attendanceResponseList = attendanceService.getAttendanceByLecture(lectureId, date);

        return ResponseEntity.ok(attendanceResponseList);
    }

    // 출석 정보 수정
    @PatchMapping("/update")
    @Operation(summary = "출석 정보 수정", description = "출석 정보를 수정합니다.")
    public ResponseEntity<Void> updateAttendance(List<AttendanceUpdateRequest> requestList) {

        attendanceService.updateAttendance(requestList);

        return ResponseEntity.ok().build();
    }
}
