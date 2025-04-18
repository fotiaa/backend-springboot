package com.example.todoapp.service.user;

import com.example.todoapp.model.User;
import com.example.todoapp.notification.dto.NotificationPreferences;
import com.example.todoapp.payload.SignupRequest;
import com.example.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User updateUserNotificationPreferences(String userId, NotificationPreferences preferences) {
        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update notification preferences
        user.setNotificationPreferences(preferences);

        // Save and return the updated user
        return userRepository.save(user);
    }
}