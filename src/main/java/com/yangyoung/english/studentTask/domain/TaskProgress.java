package com.yangyoung.english.studentTask.domain;

import lombok.Getter;

@Getter
public enum TaskProgress {

    NOT_STARTED(1, "제출 전"),
    COMPLETED(2, "제출 완료");

    private final int progressNumber;
    private final String progressName;

    TaskProgress(int progressNumber, String progressName) {
        this.progressNumber = progressNumber;
        this.progressName = progressName;
    }

    public static TaskProgress getTaskProgress(String progressName) {
        return switch (progressName) {
            case "제출 전" -> NOT_STARTED;
            case "제출 완료" -> COMPLETED;
            default -> null;
        };
    }
}
