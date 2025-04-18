package com.example.todoapp.notification.service;

import com.example.todoapp.notification.dto.SmsDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Override
    public void sendSms(SmsDetails details) {
        try {
            // For actual implementation, you would use Twilio API like this:
            /*
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                    new PhoneNumber(details.getPhoneNumber()),
                    new PhoneNumber(twilioPhoneNumber),
                    details.getMessage())
                .create();
            logger.info("SMS sent, SID: {}", message.getSid());
            */

            // For now, just log that we would send a message
            logger.info("SMS would be sent to {} with message: {}",
                    details.getPhoneNumber(),
                    details.getMessage());

        } catch (Exception e) {
            logger.error("Error sending SMS: {}", e.getMessage(), e);
        }
    }
}