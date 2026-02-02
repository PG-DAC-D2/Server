package com.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        try {
            logger.info("Initializing Twilio with Account SID: {}", accountSid);
            Twilio.init(accountSid, authToken);
            logger.info("Twilio initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize Twilio: {}", e.getMessage());
        }
    }

    public boolean sendSms(String toPhoneNumber, String messageBody) {
        try {
            logger.info("Sending SMS to: {}", toPhoneNumber);

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber), // To
                    new PhoneNumber(twilioPhoneNumber), // From
                    messageBody) // Body
                    .create();

            logger.info("SMS Sent! SID: {}, Status: {}", message.getSid(), message.getStatus());
            return true;

        } catch (Exception e) {
            logger.error("Failed to send SMS to {}. Error: {}", toPhoneNumber, e.getMessage());
            return false;
        }
    }

    public boolean sendPaymentSuccessSms(String phoneNumber, Long orderId, Double amount, String currency) {
        String message = "Payment Successful: Your payment of " + amount + " " + currency + 
                        " for Order #" + orderId + " is confirmed. Thank you!";
        return sendSms(phoneNumber, message);
    }

    public boolean sendPaymentFailureSms(String phoneNumber, Long orderId) {
        String message = "Payment Failed: We could not process payment for Order #" + orderId + 
                        ". Please try again.";
        return sendSms(phoneNumber, message);
    }
}
