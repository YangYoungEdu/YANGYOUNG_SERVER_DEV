package com.yangyoung.english.appUser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {

    private Long id;

    private String username;

    private String accessToken;

    private String refreshToken;
}
