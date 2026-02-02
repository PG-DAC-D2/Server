package com.notification.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEventMessage {
    
    private String eventType;
    private Long orderId;
    
    private String paymentId; // CHANGED: Long -> String to accept "PAYD0B21D9A"
    
    private Double amount;
    private String currency;
    private String paymentMethod;
    
    @JsonAlias({"email", "userEmail"}) 
    private String userEmail;
    
    @JsonAlias({"name", "userName"}) 
    private String userName;
    
    private String phoneNumber;
    private Long timestamp;
}