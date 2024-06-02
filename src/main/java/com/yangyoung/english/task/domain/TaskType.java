package com.yangyoung.english.task.domain;

import lombok.Getter;

@Getter
public enum TaskType {

    STUDENT(1, "개인 과제"),
    LECTURE(2, "강의 과제");

    private final int typeNumber;
    private final String typeName;

    TaskType(int typeNumber, String typeName) {
        this.typeNumber = typeNumber;
        this.typeName = typeName;
    }

    public static TaskType getTaskType(String typeName) {
        return switch (typeName) {
            case "개인 과제" -> STUDENT;
            case "강의 과제" -> LECTURE;
            default -> null;
        };
    }
}
