package com.yangyoung.english.attendance.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendanceUpdateRequest {

    private Long id;

    private String attendanceType;

    private String note;

}
