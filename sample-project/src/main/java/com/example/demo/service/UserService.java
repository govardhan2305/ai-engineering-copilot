package com.example.demo.service;

public class UserService {

    public String findUser(String username) {
        return "User found: " + username;
    }

    public boolean authenticate(String username, String password) {
        return username != null && password != null;
    }
}
