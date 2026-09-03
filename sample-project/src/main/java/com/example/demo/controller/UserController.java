package com.example.demo.controller;

public class UserController {

    public String getCurrentUser() {
        return "Authenticated user information";
    }

    public String getUserProfile(String username) {
        return "Profile for user: " + username;
    }
}
