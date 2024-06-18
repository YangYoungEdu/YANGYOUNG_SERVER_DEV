package com.yangyoung.english.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {

    // JWT 인증타입: Bearer
    private String grantType;

    private String accessToken;

    private String refreshToken;
}
