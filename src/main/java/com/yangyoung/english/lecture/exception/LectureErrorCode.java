package com.yangyoung.english.lecture.exception;

import com.yangyoung.english.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LectureErrorCode implements ErrorCode {

    LECTURE_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "해당 강의명이 이미 존재합니다. (강의명: %s)"),
    LECTURE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 강의를 찾을 수 없습니다. (강의 ID: %d)");

    private final HttpStatus httpStatus;
    private final String message;

    LectureErrorCode(HttpStatus status, String message) {
        this.httpStatus = status;
        this.message = message;
    }
}
