package com.yangyoung.english.school.domain;

import lombok.Getter;

@Getter
public enum SchoolType {
    MIDDLE_SCHOOL(0, "중학교"),
    HIGH_SCHOOL(1, "고등학교"),
    ;

    private final int schoolTypeNumber;
    private final String schoolTypeName;

    SchoolType(int schoolTypeNumber, String schoolTypeName) {
        this.schoolTypeNumber = schoolTypeNumber;
        this.schoolTypeName = schoolTypeName;
    }

    public static SchoolType getSchoolTypeName(String schoolTypeName) {
        return switch (schoolTypeName) {
            case "중학교" -> MIDDLE_SCHOOL;
            case "고등학교" -> HIGH_SCHOOL;
            default -> throw new IllegalArgumentException("No enum constant with schoolTypeName: " + schoolTypeName);
        };
    }
}
