package com.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.exception.PaymentNotFoundException;
import com.payment.kafka.PaymentEventProducer;
import com.payment.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

/**
 * Unit tests for Payment Service
 * Tests the core business logic of payment processing
 */
public class PaymentServiceTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private PaymentEventProducer eventProducer;
    
    @InjectMocks
    private PaymentService paymentService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testProcessPayment_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,                    // orderId
                5000.0,               // amount
                "INR",                // currency
                "CARD",               // paymentMethod
                "test@example.com",   // userEmail
                "John Doe",           // userName
                "9876543210"          // phoneNumber
        );
        
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus("SUCCESS");
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        
        // Act
        PaymentResponse response = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(5000.0, response.getAmount());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventProducer, times(1)).publishPaymentSuccessEvent(any());
    }
    
    @Test
    public void testProcessPayment_WithNullEmail_ShouldFail() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                5000.0,
                "INR",
                "CARD",
                null,          // null email - should fail
                "John Doe",
                "9876543210"
        );
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            paymentService.processPayment(request);
        });
    }
    
    @Test
    public void testGetPaymentById_Success() {
        // Arrange
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(1L);
        payment.setAmount(5000.0);
        payment.setStatus("SUCCESS");
        payment.setPaymentId("PAY12345678");
        
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        
        // Act
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(5000.0, response.getAmount());
    }
    
    @Test
    public void testGetPaymentById_NotFound() {
        // Arrange
        Long paymentId = 999L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentById(paymentId);
        });
    }
    
    @Test
    public void testProcessPayment_NegativeAmount_ShouldFail() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                -5000.0,              // Negative amount - invalid
                "INR",
                "CARD",
                "test@example.com",
                "John Doe",
                "9876543210"
        );
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            paymentService.processPayment(request);
        });
    }
    
    @Test
    public void testProcessPayment_InvalidCurrency_ShouldFail() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                5000.0,
                "INVALID",            // Invalid 3-character currency code
                "CARD",
                "test@example.com",
                "John Doe",
                "9876543210"
        );
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            paymentService.processPayment(request);
        });
    }
    
    @Test
    public void testGetPaymentByOrderId_Success() {
        // Arrange
        Long orderId = 1L;
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(5000.0);
        payment.setStatus("SUCCESS");
        
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        
        // Act
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        
        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
    }
}
