package com.yangyoung.english.appUser.exception;

public class InvalidTokenException extends RuntimeException {

    public AppUserErrorCode appUserErrorCode;

    public InvalidTokenException(AppUserErrorCode appUserErrorCode) {
        super(appUserErrorCode.getMessage());
        this.appUserErrorCode = appUserErrorCode;
    }
}
