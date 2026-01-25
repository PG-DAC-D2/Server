package com.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.dto.NotificationRequest;
import com.notification.dto.PaymentEventMessage;
import com.notification.service.EmailService;
import com.notification.service.SmsService;
import com.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-events", groupId = "notification-service", 
                   autoStartup = "false")
    public void consumePaymentEvent(String message) {
        try {
            logger.info("Received payment event: {}", message);

            PaymentEventMessage paymentEvent = objectMapper.readValue(message, PaymentEventMessage.class);

            if ("PAYMENT_SUCCESS".equals(paymentEvent.getEventType())) {
                handlePaymentSuccess(paymentEvent);
            } else if ("PAYMENT_FAILED".equals(paymentEvent.getEventType())) {
                handlePaymentFailure(paymentEvent);
            }

        } catch (Exception e) {
            logger.error("Error processing payment event", e);
        }
    }

    private void handlePaymentSuccess(PaymentEventMessage event) {
        logger.info("Processing PAYMENT_SUCCESS event for order: {}", event.getOrderId());

        // Send email notification
        boolean emailSent = emailService.sendPaymentSuccessEmail(
                event.getUserEmail(),
                event.getUserName(),
                event.getOrderId(),
                event.getAmount(),
                event.getCurrency()
        );

        // Send SMS notification
        boolean smsSent = false;
        if (event.getPhoneNumber() != null && !event.getPhoneNumber().isEmpty()) {
            smsSent = smsService.sendPaymentSuccessSms(
                    event.getPhoneNumber(),
                    event.getOrderId(),
                    event.getAmount(),
                    event.getCurrency()
            );
        }

        // Save email notification record
        NotificationRequest emailRequest = new NotificationRequest();
        emailRequest.setRecipientEmail(event.getUserEmail());
        emailRequest.setSubject("Payment Successful - Order #" + event.getOrderId());
        emailRequest.setMessage("Your payment of " + event.getAmount() + " " + event.getCurrency() + 
                               " for Order #" + event.getOrderId() + " has been processed successfully.");
        emailRequest.setNotificationType("EMAIL");
        emailRequest.setOrderId(event.getOrderId());

        notificationService.sendNotification(emailRequest);

        // Save SMS notification record if phone number provided
        if (event.getPhoneNumber() != null && !event.getPhoneNumber().isEmpty()) {
            NotificationRequest smsRequest = new NotificationRequest();
            smsRequest.setRecipientEmail(event.getUserEmail());
            smsRequest.setPhoneNumber(event.getPhoneNumber());
            smsRequest.setSubject("Payment Successful");
            smsRequest.setMessage("Your payment of " + event.getAmount() + " " + event.getCurrency() +
                                 " for Order #" + event.getOrderId() + " has been processed successfully.");
            smsRequest.setNotificationType("SMS");
            smsRequest.setOrderId(event.getOrderId());
            notificationService.sendNotification(smsRequest);
        }

        logger.info("Payment success notifications sent - Email: {}, SMS: {}", emailSent, smsSent);
    }

    private void handlePaymentFailure(PaymentEventMessage event) {
        logger.info("Processing PAYMENT_FAILED event for order: {}", event.getOrderId());

        // Send email notification
        boolean emailSent = emailService.sendPaymentFailureEmail(
                event.getUserEmail(),
                event.getUserName(),
                event.getOrderId()
        );

        // Send SMS notification
        boolean smsSent = false;
        if (event.getPhoneNumber() != null && !event.getPhoneNumber().isEmpty()) {
            smsSent = smsService.sendPaymentFailureSms(
                    event.getPhoneNumber(),
                    event.getOrderId()
            );
        }

        // Save email notification record
        NotificationRequest emailRequest = new NotificationRequest();
        emailRequest.setRecipientEmail(event.getUserEmail());
        emailRequest.setSubject("Payment Failed - Order #" + event.getOrderId());
        emailRequest.setMessage("Unfortunately, your payment for Order #" + event.getOrderId() + 
                               " could not be processed. Please try again.");
        emailRequest.setNotificationType("EMAIL");
        emailRequest.setOrderId(event.getOrderId());

        notificationService.sendNotification(emailRequest);

        // Save SMS notification record if phone number provided
        if (event.getPhoneNumber() != null && !event.getPhoneNumber().isEmpty()) {
            NotificationRequest smsRequest = new NotificationRequest();
            smsRequest.setRecipientEmail(event.getUserEmail());
            smsRequest.setPhoneNumber(event.getPhoneNumber());
            smsRequest.setSubject("Payment Failed");
            smsRequest.setMessage("Payment failed for Order #" + event.getOrderId() +
                                 ". Please try again or contact support.");
            smsRequest.setNotificationType("SMS");
            smsRequest.setOrderId(event.getOrderId());
            notificationService.sendNotification(smsRequest);
        }

        logger.info("Payment failure notifications sent - Email: {}, SMS: {}", emailSent, smsSent);
    }
}
