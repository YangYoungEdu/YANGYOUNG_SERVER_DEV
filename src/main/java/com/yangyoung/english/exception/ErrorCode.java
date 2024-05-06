package com.yangyoung.english.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();

    String getMessage();

    HttpStatus getHttpStatus();
}
