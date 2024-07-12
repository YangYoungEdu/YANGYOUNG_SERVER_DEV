package com.yangyoung.english.appUser.dto.response;

import com.yangyoung.english.auth.dto.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {

    private Long id;

    private String username;

    private JwtToken jwtToken;
}
