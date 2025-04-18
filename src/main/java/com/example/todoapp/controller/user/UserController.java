package com.example.todoapp.controller.user;

import com.example.todoapp.model.User;
import com.example.todoapp.notification.dto.NotificationPreferences;
import com.example.todoapp.service.user.UserService;
import com.example.todoapp.utils.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskUtil taskUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Remove sensitive information
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> {
                    user.setPassword(null); // Remove sensitive information
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> getCurrentUser(@RequestParam String username) {
        return userService.getUserByUsername(username)
                .map(user -> {
                    user.setPassword(null); // Remove sensitive information
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/notification-preferences")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NotificationPreferences> updateNotificationPreferences(
            @RequestBody NotificationPreferences preferences) {
        String userId = taskUtil.getCurrentUserId();

        // Update user preferences in the user service
        User updatedUser = userService.updateUserNotificationPreferences(userId, preferences);
        
        return ResponseEntity.ok(updatedUser.getNotificationPreferences());
    }
}