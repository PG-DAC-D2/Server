package com.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public boolean sendEmail(String recipientEmail, String subject, String message) {
        try {
            if (mailSender == null) {
                logger.warn("JavaMailSender not configured. Simulating email send to: {}", recipientEmail);
                logger.info("Subject: {}", subject);
                logger.info("Message: {}", message);
                return true;
            }

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(recipientEmail);
            email.setSubject(subject);
            email.setText(message);
            email.setFrom("noreply@ecommerce.com");

            mailSender.send(email);
            logger.info("Email sent successfully to: {}", recipientEmail);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", recipientEmail, e);
            return false;
        }
    }

    public boolean sendPaymentSuccessEmail(String recipientEmail, String userName, 
                                           Long orderId, Double amount, String currency) {
        String subject = "Payment Successful - Order #" + orderId;
        String message = "Dear " + userName + ",\n\n" +
                        "Your payment of " + amount + " " + currency + " has been processed successfully.\n" +
                        "Order ID: " + orderId + "\n\n" +
                        "Thank you for your purchase!\n\n" +
                        "Best regards,\n" +
                        "E-commerce Team";

        return sendEmail(recipientEmail, subject, message);
    }

    public boolean sendPaymentFailureEmail(String recipientEmail, String userName, Long orderId) {
        String subject = "Payment Failed - Order #" + orderId;
        String message = "Dear " + userName + ",\n\n" +
                        "Unfortunately, your payment for Order #" + orderId + " could not be processed.\n" +
                        "Please try again or contact support for assistance.\n\n" +
                        "Best regards,\n" +
                        "E-commerce Team";

        return sendEmail(recipientEmail, subject, message);
    }
}
