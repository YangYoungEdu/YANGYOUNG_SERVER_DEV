package com.yangyoung.english.student.exception;

import com.yangyoung.english.exception.CommonException;

public class StudentNotFoundException extends CommonException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
