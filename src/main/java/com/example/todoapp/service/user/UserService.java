package com.example.todoapp.service.user;

import com.example.todoapp.model.User;
import com.example.todoapp.payload.SignupRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(SignupRequest signupRequest);
    List<User> getAllUsers();
    Optional<User> getUserById(String id);
    Optional<User> getUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}