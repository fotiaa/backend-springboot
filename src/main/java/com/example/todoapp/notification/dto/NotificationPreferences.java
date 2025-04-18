package com.example.todoapp.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferences {
    private boolean emailEnabled = true;
    private boolean smsEnabled = false;
    private boolean websocketEnabled = true;
}
