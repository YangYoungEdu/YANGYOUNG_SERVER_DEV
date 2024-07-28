package com.yangyoung.english.lecture.domain;

import lombok.Getter;

@Getter
public enum LectureType {

    GENERAL(1, "일반"),
    SPECIAL(2, "특강"),
    ;

    private final int code;
    private final String description;

    LectureType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static LectureType getLectureTypeByCode(int code) {
        return switch (code) {
            case 1 -> GENERAL;
            case 2 -> SPECIAL;
            default -> null;
        };
    }

    public static LectureType getLectureTypeByDescription(String description) {
        return switch (description) {
            case "일반" -> GENERAL;
            case "특강" -> SPECIAL;
            default -> null;
        };
    }
}
