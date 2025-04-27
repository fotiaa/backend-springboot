package com.example.todoapp.notification.service.sms;

import com.example.todoapp.notification.dto.SmsDetails;

public interface SmsService {
    void sendSms(SmsDetails details);
}