package com.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "recipientEmail cannot be blank")
    @Email(message = "recipientEmail must be a valid email address")
    private String recipientEmail;
    
    @Pattern(regexp = "^(\\+91[0-9]{10}|[0-9]{10})$", message = "phoneNumber must be 10 digits or +91 followed by 10 digits")
    private String phoneNumber;
    
    @NotBlank(message = "subject cannot be blank")
    @Size(min = 3, max = 100, message = "subject must be between 3 and 100 characters")
    private String subject;
    
    @NotBlank(message = "message cannot be blank")
    @Size(min = 10, max = 5000, message = "message must be between 10 and 5000 characters")
    private String message;
    
    @NotBlank(message = "notificationType cannot be blank")
    private String notificationType;  // EMAIL, SMS
    
    private Long orderId;
}
