package com.yangyoung.english.lecture.exception;

import com.yangyoung.english.exception.CommonException;

public class LectureNotFoundException extends CommonException {

    private final static String LECTURE_NOT_FOUND_MESSAGE = "Lecture not found. (lectureId: %d)";

    public LectureNotFoundException(String message) {
        super(message);
    }

    public LectureNotFoundException(Long id) {
        super(String.format(LECTURE_NOT_FOUND_MESSAGE, id));
    }
}
