package com.yangyoung.english.task.exception;

import com.yangyoung.english.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskErrorCode implements ErrorCode {

    TASK_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 Task를 찾을 수 없습니다. (Task ID: %d)");

    private final HttpStatus httpStatus;
    private final String message;

    TaskErrorCode(HttpStatus status, String message) {
        this.httpStatus = status;
        this.message = message;
    }
}
