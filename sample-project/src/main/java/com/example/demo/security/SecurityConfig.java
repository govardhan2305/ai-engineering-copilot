package com.example.demo.security;

public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    public String configureSecurity() {
        return "JWT authentication enabled using JwtFilter";
    }

    public JwtFilter getJwtFilter() {
        return jwtFilter;
    }
}
