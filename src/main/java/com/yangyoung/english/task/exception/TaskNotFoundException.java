package com.yangyoung.english.task.exception;

import lombok.Getter;

@Getter
public class TaskNotFoundException extends RuntimeException {

    public TaskErrorCode taskErrorCode;

    public TaskNotFoundException(TaskErrorCode taskErrorCode, Long id) {
        super(String.format(taskErrorCode.getMessage(), id));
        this.taskErrorCode = taskErrorCode;
    }
}
