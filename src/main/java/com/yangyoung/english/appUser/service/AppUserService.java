package com.yangyoung.english.appUser.service;

import com.yangyoung.english.appUser.domain.AppUserRepository;
import com.yangyoung.english.appUser.exception.AppUserErrorCode;
import com.yangyoung.english.appUser.exception.InvalidTokenException;
import com.yangyoung.english.appUser.exception.PasswordNotMatchException;
import com.yangyoung.english.appUser.exception.UserNotFoundException;
import com.yangyoung.english.auth.JwtTokenProvider;
import com.yangyoung.english.auth.TokenBlacklistService;
import com.yangyoung.english.auth.dto.JwtToken;
import com.yangyoung.english.auth.dto.SignOutDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public JwtToken signIn(String username, String password) {

        if (!appUserRepository.existsByUsername(username)) {
            AppUserErrorCode appUserErrorCode = AppUserErrorCode.USERNAME_NOT_FOUND;
            throw new UserNotFoundException(appUserErrorCode, username);
        }
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
            // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            return jwtTokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            AppUserErrorCode appUserErrorCode = AppUserErrorCode.PASSWORD_NOT_MATCH;
            throw new PasswordNotMatchException(appUserErrorCode, password);
        } catch (AuthenticationException e) {
            // 그 외 인증 관련 예외 처리
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    public void signOut(SignOutDto signOutDto) {
        if (!jwtTokenProvider.validateToken(signOutDto.getAccessToken())) { // accessToken 이 유효하지 않은 경우
            throw new InvalidTokenException(AppUserErrorCode.INVALID_TOKEN);
        }

        if (!jwtTokenProvider.validateToken(signOutDto.getRefreshToken())) { // refreshToken 이 유효하지 않은 경우
            throw new InvalidTokenException(AppUserErrorCode.INVALID_TOKEN);
        }

        tokenBlacklistService.blacklistToken(signOutDto.getAccessToken());
    }
}