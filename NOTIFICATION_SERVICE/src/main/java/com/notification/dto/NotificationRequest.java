package com.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String recipientEmail;
    private String phoneNumber;
    private String subject;
    private String message;
    private String notificationType;  // EMAIL, SMS
    private Long orderId;
}
