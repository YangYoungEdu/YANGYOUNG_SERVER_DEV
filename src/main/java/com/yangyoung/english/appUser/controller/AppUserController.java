package com.yangyoung.english.appUser.controller;

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
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();
        JwtToken jwtToken = appUserService.signIn(username, password);
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
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
