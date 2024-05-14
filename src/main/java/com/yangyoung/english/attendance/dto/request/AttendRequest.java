package com.yangyoung.english.attendance.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendRequest {

    private Long studentId;

    private Long lectureId;
}
