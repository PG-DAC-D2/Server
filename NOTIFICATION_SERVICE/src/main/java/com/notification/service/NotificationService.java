package com.notification.service;

import com.notification.dto.NotificationRequest;
import com.notification.entity.Notification;
import com.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
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

        // 1. Save initial PENDING state
        Notification savedNotification = notificationRepository.save(notification);

        boolean sent = false;
        try {
            if ("EMAIL".equalsIgnoreCase(request.getNotificationType())) {
                sent = emailService.sendEmail(
                        request.getRecipientEmail(),
                        request.getSubject(),
                        request.getMessage()
                );
            } else if ("SMS".equalsIgnoreCase(request.getNotificationType())) {
                if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
                    logger.error("Cannot send SMS: Phone number is missing");
                    sent = false;
                } else {
                    sent = smsService.sendSms(request.getPhoneNumber(), request.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error during notification send", e);
            sent = false;
        }

        // 2. Update status based on result
        savedNotification.setStatus(sent ? "SENT" : "FAILED");
        savedNotification.setSentAt(sent ? LocalDateTime.now() : null);
        
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
