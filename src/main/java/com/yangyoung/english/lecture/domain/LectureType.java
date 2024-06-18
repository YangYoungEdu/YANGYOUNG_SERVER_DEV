package com.yangyoung.english.lecture.domain;

import lombok.Getter;

@Getter
public enum LectureType {

    PRE(0, "예습"),
    CLASS(1, "수업"),
    TEXTBOOK(2, "교과서"),
    MOCK(3, "모의고사"),
    SUPPLEMENT(4, "보충교재"),
    CHECK(5, "확인학습"),
    ;

    private final int lectureTypeNumber;
    private final String lectureTypeName;

    LectureType(int lectureTypeNumber, String lectureTypeName) {
        this.lectureTypeNumber = lectureTypeNumber;
        this.lectureTypeName = lectureTypeName;
    }

    public static LectureType getLectureTypeName(String lectureTypeName) {
        return switch (lectureTypeName) {
            case "PRE" -> PRE;
            case "CLASS" -> CLASS;
            case "교과서" -> TEXTBOOK;
            case "모의고사" -> MOCK;
            case "보충교재" -> SUPPLEMENT;
            case "확인학습" -> CHECK;
            default -> throw new IllegalArgumentException("No enum constant with lectureTypeName: " + lectureTypeName);
        };
    }
}
