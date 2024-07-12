package com.yangyoung.english.student.exception;

public class InvalidStudentIdException extends RuntimeException{

    public StudentErrorCode studentErrorCode;

    public InvalidStudentIdException(StudentErrorCode studentErrorCode, String id) {
        super(String.format(studentErrorCode.getMessage(), id));
        this.studentErrorCode = studentErrorCode;
    }
}
