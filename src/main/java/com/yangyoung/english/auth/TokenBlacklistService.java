package com.yangyoung.english.auth;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private Set<String> blacklist = new HashSet<>();

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }

    public void blacklistToken(String token) {
        blacklist.add(token);
    }
}