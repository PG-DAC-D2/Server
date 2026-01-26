package com.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventMessage {
    private String eventType;        // PAYMENT_SUCCESS, PAYMENT_FAILED
    private Long orderId;
    private Long paymentId;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String userEmail;
    private String userName;
    private String phoneNumber;     // NEW: For SMS notifications
    private Long timestamp;
}
