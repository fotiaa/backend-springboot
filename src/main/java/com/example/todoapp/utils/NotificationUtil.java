package com.example.todoapp.utils;

import com.example.todoapp.model.Task;
import com.example.todoapp.model.User;
import com.example.todoapp.notification.dto.EmailDetails;
import com.example.todoapp.notification.dto.NotificationPreferences;
import com.example.todoapp.notification.dto.SmsDetails;
import com.example.todoapp.notification.service.EmailService;
import com.example.todoapp.notification.service.SmsService;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.service.task.TaskAsyncService;
import com.example.todoapp.websocket.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationUtil {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAsyncService taskAsyncService;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserRepository userRepository;

    public void sendTaskUpdateNotification(Task task, String action, String userId) {
        // Get user preferences
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return;
        }

        User user = userOpt.get();
        NotificationPreferences prefs = user.getNotificationPreferences();

        // Build notification message
        String message = String.format("Task '%s' has been %s", task.getTitle(), action.toLowerCase());

        // Send WebSocket notification
        if (prefs.isWebsocketEnabled()) {
            WebSocketMessage webSocketMessage = new WebSocketMessage(
                    action,
                    task.getId(),
                    message,
                    task
            );
            messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", webSocketMessage);
        }

        // Send email notification
        if (prefs.isEmailEnabled()) {
            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setRecipient(user.getEmail());
            emailDetails.setSubject("Todo App: Task " + action);
            emailDetails.setBody(message + "\n\nTask details:\n" +
                    "Title: " + task.getTitle() + "\n" +
                    "Status: " + task.getStatus() + "\n" +
                    "Description: " + task.getDescription());

            emailService.sendEmail(emailDetails);
        }

        // Send SMS notification
        if (prefs.isSmsEnabled() && user.getPhoneNumber() != null) {
            SmsDetails smsDetails = new SmsDetails();
            smsDetails.setPhoneNumber(user.getPhoneNumber());
            smsDetails.setMessage(message);

            smsService.sendSms(smsDetails);
        }
    }
}
