package com.yangyoung.english.attendance.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AttendRequest {

    private Long studentId;

    private LocalDateTime attendTime;
}
