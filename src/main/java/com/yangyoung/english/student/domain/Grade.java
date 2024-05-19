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

    public static Grade fromGradeName(String gradeName) {
        return switch (gradeName) {
            case "중3" -> M3;
            case "고1" -> H1;
            case "고2" -> H2;
            case "고3" -> H3;
            default -> throw new IllegalArgumentException("No enum constant with gradeName: " + gradeName);
        };
    }
}
