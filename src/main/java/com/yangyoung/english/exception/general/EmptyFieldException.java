package com.yangyoung.english.exception.general;

import org.apache.poi.EmptyFileException;

public class EmptyFieldException extends RuntimeException {

    public final static String MESSAGE = "필수 입력값이 비어있습니다. (필드: %s)";

    public EmptyFieldException(String field) {
        super(MESSAGE + field);
    }
}
