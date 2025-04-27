package com.example.todoapp.notification.service.notification;

import com.example.todoapp.notification.dto.NotificationRequest;

public interface NotificationService {
    void processNotification(NotificationRequest request);
}