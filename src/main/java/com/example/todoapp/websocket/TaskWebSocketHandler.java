package com.example.todoapp.websocket;

import com.example.todoapp.notification.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.todoapp.model.Task;

@Component
public class TaskWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(TaskWebSocketHandler.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendTaskUpdate(String userId, Task task, String action) {
        try {
            WebSocketMessage message = new WebSocketMessage(
                    action,
                    task.getId(),
                    "Task " + action.toLowerCase(),
                    task
            );

            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/notifications",
                    message
            );

            // Also send to a general topic for admin monitoring (optional)
            messagingTemplate.convertAndSend(
                    "/topic/tasks",
                    message
            );

            logger.info("WebSocket message sent to user {}: {}", userId, message);
        } catch (Exception e) {
            logger.error("Error sending WebSocket message: {}", e.getMessage(), e);
        }
    }

    public void sendNotification(NotificationRequest request) {
        try {
            // Extract the WebSocketMessage from the request data field
            if (request.getData() instanceof WebSocketMessage) {
                WebSocketMessage wsMessage = (WebSocketMessage) request.getData();

                messagingTemplate.convertAndSendToUser(
                        request.getUserId(),
                        "/queue/notifications",
                        wsMessage
                );

                logger.info("WebSocket notification sent to user {}: {}",
                        request.getUserId(), request.getMessage());
            } else {
                // If data is not a WebSocketMessage, create a simple message
                WebSocketMessage message = new WebSocketMessage(
                        "NOTIFICATION",
                        null,
                        request.getMessage(),
                        null
                );

                messagingTemplate.convertAndSendToUser(
                        request.getUserId(),
                        "/queue/notifications",
                        message
                );

                logger.info("Simple WebSocket notification sent to user {}: {}",
                        request.getUserId(), request.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error sending WebSocket notification: {}", e.getMessage(), e);
        }
    }
}