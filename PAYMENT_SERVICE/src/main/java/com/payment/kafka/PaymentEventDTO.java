package com.payment.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDTO {
    private String eventType;        // PAYMENT_SUCCESS, PAYMENT_FAILED
    private Long orderId;
    private Long paymentId;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String userEmail;
    private String userName;
    private String phoneNumber;
    private Long timestamp;
}
