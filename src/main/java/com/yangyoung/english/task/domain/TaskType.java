package com.yangyoung.english.task.domain;

import lombok.Getter;

@Getter
public enum TaskType {

    STUDENT(1, "개인"),
    LECTURE(2, "강의");

    private final int typeNumber;
    private final String typeName;

    TaskType(int typeNumber, String typeName) {
        this.typeNumber = typeNumber;
        this.typeName = typeName;
    }
}
