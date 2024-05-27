package com.yangyoung.english.studentTask.domain;

import lombok.Getter;

@Getter
public enum TaskProgress {

    NOT_STARTED(1, "시작전"),
    IN_PROGRESS(2, "진행중"),
    COMPLETED(3, "완료");

    private final int progressNumber;
    private final String progressName;

    TaskProgress(int progressNumber, String progressName) {
        this.progressNumber = progressNumber;
        this.progressName = progressName;
    }

    public static TaskProgress getTaskProgress(String progressName) {
        return switch (progressName) {
            case "시작전" -> NOT_STARTED;
            case "진행중" -> IN_PROGRESS;
            case "완료" -> COMPLETED;
            default -> null;
        };
    }
}
