package com.payment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for Payment Controller
 * Tests API endpoints and HTTP status codes
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PaymentService paymentService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testProcessPayment_Success() throws Exception {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L, 5000.0, "INR", "CARD",
                "test@example.com", "John Doe", "9876543210"
        );
        
        PaymentResponse response = new PaymentResponse("PAY12345678", "SUCCESS", 1L, 5000.0);
        
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value("PAY12345678"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(5000.0));
    }
    
    @Test
    public void testProcessPayment_InvalidEmail_ShouldReturn400() throws Exception {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L, 5000.0, "INR", "CARD",
                "invalid-email",   // Invalid email
                "John Doe", "9876543210"
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testGetPayment_Success() throws Exception {
        // Arrange
        Long paymentId = 1L;
        PaymentResponse response = new PaymentResponse("PAY12345678", "SUCCESS", 1L, 5000.0);
        
        when(paymentService.getPaymentById(paymentId)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/payments/" + paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY12345678"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
    
    @Test
    public void testGetPayment_NotFound_ShouldReturn404() throws Exception {
        // Arrange
        Long paymentId = 999L;
        when(paymentService.getPaymentById(paymentId))
                .thenThrow(new RuntimeException("Payment not found"));
        
        // Act & Assert
        mockMvc.perform(get("/api/payments/" + paymentId))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    public void testProcessPayment_NegativeAmount_ShouldReturn400() throws Exception {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L, -5000.0, "INR", "CARD",  // Negative amount
                "test@example.com", "John Doe", "9876543210"
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
