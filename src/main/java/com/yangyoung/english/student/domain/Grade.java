package com.yangyoung.english.student.domain;

import lombok.Getter;

@Getter
public enum Grade {

    H1(2, "고1", "1학년"),
    H2(3, "고2", "2학년"),
    H3(4, "고3", "3학년"),
    ;

    private final int gradeNumber;
    private final String gradeName;
    private final String secondGradeName;

    Grade(int gradeNumber, String gradeName, String secondGradeName) {
        this.gradeNumber = gradeNumber;
        this.gradeName = gradeName;
        this.secondGradeName = secondGradeName;
    }

    public static Grade getGradeName(String gradeName) {
        return switch (gradeName) {
            case "고1" -> H1;
            case "고2" -> H2;
            case "고3" -> H3;
            default -> throw new IllegalArgumentException("No enum constant with gradeName: " + gradeName);
        };
    }

    public static Grade getSecondGradeName(String secondGradeName) {
        return switch (secondGradeName) {
            case "1학년" -> H1;
            case "2학년" -> H2;
            case "3학년" -> H3;
            default -> throw new IllegalArgumentException("No enum constant with secondGradeName: " + secondGradeName);
        };
    }
}
