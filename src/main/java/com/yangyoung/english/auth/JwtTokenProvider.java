package com.yangyoung.english.auth;

import com.yangyoung.english.auth.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final static Long TWO_HOURS = 7200000L;
    private final static Long TWO_WEEKS = 86400000 * 14L;
    private final Key key;
    private final String secretKey;


    // application.yaml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        log.info("secretKey: {}", secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.secretKey = secretKey;
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {

        // 권한 가져오기
        Optional<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")).describeConstable();
        if (authorities.isEmpty()) {
            throw new RuntimeException("권한 정보가 없습니다.");
        } else {
            log.info("authorities list: {}", authorities.get());
        }

        // 현재 시각 가져오기
        Instant now = Instant.now();
        // Access Token 만료 시각 계산
        Instant accessTokenExpiresIn = now.plus(TWO_HOURS, ChronoUnit.HOURS);
        Instant refreshTokenExpiresIn = now.plus(TWO_WEEKS, ChronoUnit.DAYS);
        // Date 객체로 변환 (예시로 Date 객체로 변환하는 경우)
        Date accessTokenExpiresInDate = Date.from(accessTokenExpiresIn);
        Date refreshTokenExpiresInDate = Date.from(refreshTokenExpiresIn);

        // Access Token 생성
        Optional<String> accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", authorities.get())
                .setExpiration(accessTokenExpiresInDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact().describeConstable();
        if (accessToken.isEmpty()) {
            throw new RuntimeException("Access Token 생성 실패");
        }

        // Refresh Token 생성
        Optional<String> refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresInDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact().describeConstable();
        if (refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh Token 생성 실패");
        }

        Optional<JwtToken> jwtToken = Optional.of(JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken.get())
                .refreshToken(refreshToken.get())
                .build());

        return jwtToken.get();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {

        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("roles") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getExpiration(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.getExpiration().getTime();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().
                setSigningKey(secretKey).
                build().
                parseClaimsJws(token).
                getBody().
                getSubject();
    }

    public List<String> getRoles(String token) {
        List<String> roles = new ArrayList<>();

        Claims claims = Jwts.
                parserBuilder().
                setSigningKey(secretKey).
                build().
                parseClaimsJws(token).
                getBody();

        System.out.println(claims.get("roles").toString());

//        roles = roleList.get();

        return roles;
    }
}