package com.example.todoapp.websocket;

import com.example.todoapp.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String action; // CREATED, UPDATED, DELETED
    private String taskId;
    private String message;
    private Task task;
}