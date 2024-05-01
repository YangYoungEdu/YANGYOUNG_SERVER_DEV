package com.yangyoung.english.student.domain;

import lombok.Getter;

@Getter
public enum Grade {

    M3(1, "중3"),
    H1(2, "고1"),
    H2(3, "고2"),
    H3(4, "고3"),
    ;

    private final int gradeNumber;
    private final String gradeName;

    Grade(int gradeNumber, String gradeName) {
        this.gradeNumber = gradeNumber;
        this.gradeName = gradeName;
    }
}
