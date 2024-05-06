package com.yangyoung.english.student.exception;

import com.yangyoung.english.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StudentErrorCode implements ErrorCode {

    STUDENT_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "해당 아이디가 이미 존재합니다. (ID: %s)"),
    STUDENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 학생을 찾을 수 없습니다. (학생 ID: %d)");

    private final HttpStatus httpStatus;
    private final String message;

    StudentErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = message;
    }
}
