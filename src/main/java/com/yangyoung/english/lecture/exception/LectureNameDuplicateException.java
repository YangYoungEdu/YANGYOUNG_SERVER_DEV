package com.yangyoung.english.lecture.exception;

import lombok.Getter;

@Getter
public class LectureNameDuplicateException extends RuntimeException {

    public LectureErrorCode lectureErrorCode;

    public LectureNameDuplicateException(LectureErrorCode lectureErrorCode, String name) {
        super(String.format(lectureErrorCode.getMessage(), name));
        this.lectureErrorCode = lectureErrorCode;
    }
}
