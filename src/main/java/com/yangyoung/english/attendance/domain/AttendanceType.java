package com.yangyoung.english.attendance.domain;

import lombok.Getter;

@Getter
public enum AttendanceType {

    ATTENDANCE(1, "출석"),
    ABSENCE(2, "결석"),
    LATE(3, "지각"),
    ;

    private final int typeNumber;
    private final String typeName;

    AttendanceType(int typeNumber, String typeName) {
        this.typeNumber = typeNumber;
        this.typeName = typeName;
    }
}
