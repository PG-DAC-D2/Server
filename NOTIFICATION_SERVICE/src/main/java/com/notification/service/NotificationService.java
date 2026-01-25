package com.notification.service;

import com.notification.dto.NotificationRequest;
import com.notification.entity.Notification;
import com.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public Notification sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setOrderId(request.getOrderId());
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setPhoneNumber(request.getPhoneNumber());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setNotificationType(request.getNotificationType());
        notification.setStatus("PENDING");

        // Save to database first
        Notification savedNotification = notificationRepository.save(notification);

        // Send notification based on type
        boolean sent = false;
        if ("EMAIL".equalsIgnoreCase(request.getNotificationType())) {
            sent = emailService.sendEmail(
                    request.getRecipientEmail(),
                    request.getSubject(),
                    request.getMessage()
            );
        } else if ("SMS".equalsIgnoreCase(request.getNotificationType())) {
            // Use actual Twilio SMS service
            sent = smsService.sendSms(request.getPhoneNumber(), request.getMessage());
        }

        // Update status
        if (sent) {
            savedNotification.setStatus("SENT");
            logger.info("Notification sent for order: {}", request.getOrderId());
        } else {
            savedNotification.setStatus("FAILED");
            logger.error("Failed to send notification for order: {}", request.getOrderId());
        }

        return notificationRepository.save(savedNotification);
    }

    public List<Notification> getNotificationsByOrderId(Long orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatus("PENDING");
    }

    public List<Notification> getFailedNotifications() {
        return notificationRepository.findByStatus("FAILED");
    }

    public List<Notification> getNotificationsByEmail(String email) {
        return notificationRepository.findByRecipientEmail(email);
    }
}
