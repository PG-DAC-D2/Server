package com.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "orderId cannot be null")
    @Positive(message = "orderId must be positive")
    private Long orderId;
    
    @NotNull(message = "amount cannot be null")
    @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "amount cannot exceed 999999.99")
    private Double amount;
    
    @NotBlank(message = "currency cannot be blank")
    @Size(min = 3, max = 3, message = "currency must be exactly 3 characters")
    private String currency;
    
    @NotBlank(message = "paymentMethod cannot be blank")
    private String paymentMethod;
    
    @NotBlank(message = "userEmail cannot be blank")
    @Email(message = "userEmail must be a valid email address")
    private String userEmail;
    
    @NotBlank(message = "userName cannot be blank")
    @Size(min = 2, max = 100, message = "userName must be between 2 and 100 characters")
    private String userName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "phoneNumber must be exactly 10 digits")
    private String phoneNumber;
}
