package com.example.todoapp.notification.service;

import com.example.todoapp.notification.dto.EmailDetails;

public interface EmailService {
    void sendEmail(EmailDetails details);
}