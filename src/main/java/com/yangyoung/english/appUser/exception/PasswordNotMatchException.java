package com.yangyoung.english.appUser.exception;

public class PasswordNotMatchException extends RuntimeException {

    public AppUserErrorCode appUserErrorCode;

    public PasswordNotMatchException(AppUserErrorCode appUserErrorCode, String password) {
        super(String.format(appUserErrorCode.getMessage(), password));
        this.appUserErrorCode = appUserErrorCode;
    }
}
