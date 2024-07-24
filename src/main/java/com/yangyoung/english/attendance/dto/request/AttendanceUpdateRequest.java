package com.yangyoung.english.attendance.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AttendanceUpdateRequest {

    private Long id;

    private Long studentId;

    private Long lectureId;

    private String attendanceType;

    private LocalDateTime attendDateTime;

    private String note;

}
