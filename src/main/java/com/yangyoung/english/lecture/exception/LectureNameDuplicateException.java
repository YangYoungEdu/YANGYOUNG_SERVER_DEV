package com.yangyoung.english.lecture.exception;

import com.yangyoung.english.exception.CommonException;

public class LectureNameDuplicateException extends CommonException {

    private final static String LECTURE_NAME_DUPLICATED_MESSAGE = "Lecture name is already exist. (lectureName: %s)";

    public LectureNameDuplicateException(String message) {
        super(message);
    }

    public static LectureNameDuplicateException of(String lectureName) {
        return new LectureNameDuplicateException(String.format(LECTURE_NAME_DUPLICATED_MESSAGE, lectureName));
    }
}
