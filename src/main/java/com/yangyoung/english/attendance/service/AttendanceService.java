package com.yangyoung.english.attendance.service;

import com.yangyoung.english.attendance.domain.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    // 출석 정보 등록
    public void attend() {

    }
}
