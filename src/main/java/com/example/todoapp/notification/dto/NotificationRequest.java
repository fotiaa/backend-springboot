package com.example.todoapp.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String userId;
    private String type; // EMAIL, SMS, WEBSOCKET
    private String message;
    private Object data;
}
