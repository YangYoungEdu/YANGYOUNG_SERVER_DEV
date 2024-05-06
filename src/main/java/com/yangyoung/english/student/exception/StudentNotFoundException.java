package com.yangyoung.english.student.exception;

import lombok.Getter;

@Getter
public class StudentNotFoundException extends RuntimeException {

    public StudentErrorCode studentErrorCode;

    public StudentNotFoundException(StudentErrorCode studentErrorCode, Long id) {
        super(String.format(studentErrorCode.getMessage(), id));
        this.studentErrorCode = studentErrorCode;
    }
}
