package com.yangyoung.english.lecture.exception;

import com.yangyoung.english.exception.CommonException;

public class LectureNameDuplicateException extends CommonException {
    public LectureNameDuplicateException(String message) {
        super(message);
    }
}
