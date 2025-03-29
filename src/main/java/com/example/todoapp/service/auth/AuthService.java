package com.example.todoapp.service.auth;

import com.example.todoapp.model.User;
import com.example.todoapp.payload.LoginRequest;
import com.example.todoapp.payload.SignupRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    User registerUser(SignupRequest signupRequest);
    Authentication authenticateUser(LoginRequest loginRequest);
    String generateJwtToken(Authentication authentication);
}