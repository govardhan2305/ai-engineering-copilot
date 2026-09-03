package com.example.demo.security;

public class JwtFilter {

    public boolean validateToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }

    public String extractUsername(String token) {
        if (!validateToken(token)) {
            return null;
        }

        return token.substring(7);
    }
}
