package com.yangyoung.english.student.exception;

import lombok.Getter;

@Getter
public class StudentIdDuplicateException extends RuntimeException {

    public StudentErrorCode studentErrorCode;

    public StudentIdDuplicateException(StudentErrorCode studentErrorCode, Long id) {
        super(String.format(studentErrorCode.getMessage(), id));
        this.studentErrorCode = studentErrorCode;
    }
}
