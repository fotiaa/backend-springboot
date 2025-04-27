package com.example.todoapp.notification.service.notification;

import com.example.todoapp.notification.dto.EmailDetails;
import com.example.todoapp.notification.dto.NotificationRequest;
import com.example.todoapp.notification.dto.SmsDetails;
import com.example.todoapp.notification.service.sms.SmsService;
import com.example.todoapp.notification.service.email.EmailService;
import com.example.todoapp.websocket.TaskWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TaskWebSocketHandler webSocketHandler;

    public void processNotification(NotificationRequest request) {
        switch(request.getType().toUpperCase()) {
            case "EMAIL":
                if (request.getData() instanceof EmailDetails) {
                    emailService.sendEmail((EmailDetails) request.getData());
                } else {
                    // Create EmailDetails from the message
                    EmailDetails details = new EmailDetails();
                    details.setRecipient(request.getUserId()); // Assuming userId contains email
                    details.setSubject("Task Notification");
                    details.setBody(request.getMessage());
                    emailService.sendEmail(details);
                }
                break;

            case "SMS":
                if (request.getData() instanceof SmsDetails) {
                    smsService.sendSms((SmsDetails) request.getData());
                } else {
                    // Create SmsDetails from the message
                    SmsDetails details = new SmsDetails();
                    details.setPhoneNumber(request.getUserId()); // Assuming userId contains phone
                    details.setMessage(request.getMessage());
                    smsService.sendSms(details);
                }
                break;

            case "WEBSOCKET":
                // Process websocket notification
                // This would require modifying the WebSocketHandler to accept NotificationRequest
                webSocketHandler.sendNotification(request);
                break;

            default:
                throw new IllegalArgumentException("Unknown notification type: " + request.getType());
        }
    }
}
