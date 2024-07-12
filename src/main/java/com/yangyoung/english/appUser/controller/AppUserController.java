package com.yangyoung.english.appUser.controller;

import com.yangyoung.english.appUser.dto.response.SignInResponse;
import com.yangyoung.english.appUser.service.AppUserService;
import com.yangyoung.english.auth.dto.JwtToken;
import com.yangyoung.english.auth.dto.SignInDto;
import com.yangyoung.english.auth.dto.SignOutDto;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/appUser")
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();
        SignInResponse response = appUserService.signIn(username, password);
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", response.getJwtToken().getAccessToken(), response.getJwtToken().getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestBody SignOutDto signOutDto) {
        appUserService.signOut(signOutDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }
}
