package com.yangyoung.english.student.exception;

import lombok.Getter;

@Getter
public class StudentNotFoundException extends RuntimeException {

    public StudentErrorCode studentErrorCode;

    public StudentNotFoundException(StudentErrorCode studentErrorCode, Long id) {
        super(String.format(studentErrorCode.getMessage(), id));
        this.studentErrorCode = studentErrorCode;
    }

    public StudentNotFoundException(StudentErrorCode studentErrorCode, String name) {
        super(String.format(studentErrorCode.getMessage(), name));
        this.studentErrorCode = studentErrorCode;
    }
}
