package com.yangyoung.english.appUser.exception;

import com.yangyoung.english.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppUserErrorCode implements ErrorCode {

    USERNAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 유저를 찾을 수 없습니다. (유저 ID: %s)"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    AppUserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
