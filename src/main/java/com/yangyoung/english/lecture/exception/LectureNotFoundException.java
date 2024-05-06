package com.yangyoung.english.lecture.exception;

import lombok.Getter;

@Getter
public class LectureNotFoundException extends RuntimeException {

    private final LectureErrorCode lectureErrorCode;

    public LectureNotFoundException(LectureErrorCode lectureErrorCode, Long id) {
        super(String.format(lectureErrorCode.getMessage(), id));
        this.lectureErrorCode = lectureErrorCode;
    }
}
