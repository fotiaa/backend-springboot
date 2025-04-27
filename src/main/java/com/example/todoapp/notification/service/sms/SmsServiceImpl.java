package com.example.todoapp.notification.service.sms;

import com.example.todoapp.notification.dto.SmsDetails;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
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
            // Initialize Twilio SDK
            Twilio.init(accountSid, authToken);

            // Create and send the SMS
            Message message = Message.creator(
                    new PhoneNumber(details.getPhoneNumber()), // To
                    new PhoneNumber(twilioPhoneNumber),        // From
                    details.getMessage()                       // Message
            ).create();

            logger.info("SMS sent successfully! SID: {}", message.getSid());

        } catch (Exception e) {
            logger.error("Error sending SMS: {}", e.getMessage(), e);
        }
    }
}