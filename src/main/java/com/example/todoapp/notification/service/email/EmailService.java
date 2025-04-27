package com.example.todoapp.notification.service.email;

import com.example.todoapp.notification.dto.EmailDetails;

public interface EmailService {
    void sendEmail(EmailDetails details);
}