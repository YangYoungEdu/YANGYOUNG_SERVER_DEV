package com.yangyoung.english.appUser.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    public AppUserErrorCode appUserErrorCode;

    public UserNotFoundException(AppUserErrorCode appUserErrorCode, String username) {
        super(String.format(appUserErrorCode.getMessage(), username));
        this.appUserErrorCode = appUserErrorCode;
    }
}
