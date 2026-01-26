package com.notification.controller;

import com.notification.dto.NotificationRequest;
import com.notification.entity.Notification;
import com.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest request) {
        Notification notification = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Notification>> getNotificationsByOrderId(@PathVariable Long orderId) {
        List<Notification> notifications = notificationService.getNotificationsByOrderId(orderId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Notification>> getNotificationsByEmail(@PathVariable String email) {
        List<Notification> notifications = notificationService.getNotificationsByEmail(email);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingNotifications() {
        List<Notification> notifications = notificationService.getPendingNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<Notification>> getFailedNotifications() {
        List<Notification> notifications = notificationService.getFailedNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is UP");
    }
}
