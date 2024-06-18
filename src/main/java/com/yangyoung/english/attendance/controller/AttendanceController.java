package com.yangyoung.english.attendance.controller;

import com.yangyoung.english.attendance.dto.request.AttendRequest;
import com.yangyoung.english.attendance.dto.request.AttendanceUpdateRequest;
import com.yangyoung.english.attendance.dto.response.AttendanceResponse;
import com.yangyoung.english.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출석 - 학생
    @PostMapping("/attend")
    public ResponseEntity<AttendanceResponse> attend(AttendRequest request) {

        AttendanceResponse response = attendanceService.attend(request);

        return ResponseEntity.ok(response);
    }

    // 강의별 출석 현황 조회
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByLecture(@PathVariable(value = "lectureId") Long lectureId) {

        List<AttendanceResponse> attendanceResponseList = attendanceService.getAttendanceByLecture(lectureId);

        return ResponseEntity.ok(attendanceResponseList);
    }

    // 출석 정보 수정
    @PatchMapping("/update")
    public ResponseEntity<Void> updateAttendance(List<AttendanceUpdateRequest> requestList) {

        attendanceService.updateAttendance(requestList);

        return ResponseEntity.ok().build();
    }
}
