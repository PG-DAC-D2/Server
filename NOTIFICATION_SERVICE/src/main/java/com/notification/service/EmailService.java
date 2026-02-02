package com.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public boolean sendEmail(String recipientEmail, String subject, String messageBody) {
        try {
            logger.info("Attempting to send email to: {}", recipientEmail);
            
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(senderEmail);
            email.setTo(recipientEmail);
            email.setSubject(subject);
            email.setText(messageBody);

            mailSender.send(email);
            
            logger.info("Email sent successfully to: {}", recipientEmail);
            return true;

        } catch (Exception e) {
            // Log the FULL stack trace so you can see if it is authentication or network
            logger.error("CRITICAL: Failed to send email to {}", recipientEmail, e);
            return false;
        }
    }

    public boolean sendPaymentSuccessEmail(String recipientEmail, String userName, 
                                           Long orderId, Double amount, String currency) {
        String subject = "Order Confirmation #" + orderId;
        String message = "Hi " + userName + ",\n\n" +
                        "Your payment of " + amount + " " + currency + " was successful.\n" +
                        "Order ID: " + orderId + "\n\n" +
                        "Thanks for shopping with us!";
        return sendEmail(recipientEmail, subject, message);
    }

    public boolean sendPaymentFailureEmail(String recipientEmail, String userName, Long orderId) {
        String subject = "Payment Issue - Order #" + orderId;
        String message = "Hi " + userName + ",\n\n" +
                        "Your payment for Order #" + orderId + " failed.\n" +
                        "Please check your payment method and try again.";
        return sendEmail(recipientEmail, subject, message);
    }
}
