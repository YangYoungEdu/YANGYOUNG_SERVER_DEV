package com.yangyoung.english.student.exception;

import com.yangyoung.english.exception.CommonException;

public class StudentIdDuplicateException extends CommonException {

    public StudentIdDuplicateException(String message) {
        super(message);
    }
}
