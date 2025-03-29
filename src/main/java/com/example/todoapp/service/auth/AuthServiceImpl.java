package com.example.todoapp.service.auth;

import com.example.todoapp.model.User;
import com.example.todoapp.payload.LoginRequest;
import com.example.todoapp.payload.SignupRequest;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public User registerUser(SignupRequest signupRequest) {
        // Create new user's account
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );

        Set<String> strRoles = signupRequest.getRoles();
        Set<String> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add("USER");
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        roles.add("ADMIN");
                        break;
                    default:
                        roles.add("USER");
                }
            });
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public Authentication authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        return authentication;
    }

    @Override
    public String generateJwtToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }
}