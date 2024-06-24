package com.yangyoung.english.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignOutDto {

    private String accessToken;

    private String refreshToken;
}
