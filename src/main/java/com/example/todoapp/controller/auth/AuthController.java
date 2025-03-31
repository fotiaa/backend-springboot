package com.example.todoapp.controller.auth;

import com.example.todoapp.model.User;
import com.example.todoapp.payload.JwtAuthResponse;
import com.example.todoapp.payload.LoginRequest;
import com.example.todoapp.payload.SignupRequest;
import com.example.todoapp.security.JwtTokenProvider;
import com.example.todoapp.service.auth.AuthServiceImpl;
import com.example.todoapp.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authService.authenticateUser(loginRequest);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userService.getUserByUsername(loginRequest.getUsername()).orElseThrow();

        return ResponseEntity.ok(new JwtAuthResponse(jwt, user.getUsername(), user.getRoles()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = userService.registerUser(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }
}