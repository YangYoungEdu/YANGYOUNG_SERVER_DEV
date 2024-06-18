package com.yangyoung.english.school.domain;

import lombok.Getter;

@Getter
public enum Status {

    EXAM(0, "시험기간"),
    NON_EXAM(1, "비시험기간"),
    ;

    private final int statusNumber;
    private final String statusName;

    Status(int statusNumber, String statusName) {
        this.statusNumber = statusNumber;
        this.statusName = statusName;
    }

    public static Status getStatusName(String statusName) {
        return switch (statusName) {
            case "시험기간" -> EXAM;
            case "비시험기간" -> NON_EXAM;
            default -> throw new IllegalArgumentException("No enum constant with statusName: " + statusName);
        };
    }
}
