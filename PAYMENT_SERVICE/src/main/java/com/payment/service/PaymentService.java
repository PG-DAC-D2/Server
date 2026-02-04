package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;
import com.payment.kafka.PaymentEventDTO;
import com.payment.kafka.PaymentEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventProducer eventProducer;

    public PaymentResponse processPayment(PaymentRequest request) {
        // Generate unique payment ID
        String paymentId = "PAY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create payment entity
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setPaymentId(paymentId);

        // Save to database
        Payment savedPayment = paymentRepository.save(payment);

        // Publish success event with customer data
        PaymentEventDTO event = new PaymentEventDTO();
        event.setEventType("PAYMENT_SUCCESS");
        event.setOrderId(savedPayment.getOrderId());
        // Use the String paymentId directly to avoid fragile parsing logic
        event.setPaymentId(savedPayment.getPaymentId());
        event.setAmount(savedPayment.getAmount());
        event.setCurrency(savedPayment.getCurrency());
        event.setPaymentMethod(savedPayment.getPaymentMethod());
        event.setUserEmail(request.getUserEmail());
        event.setUserName(request.getUserName());
        event.setPhoneNumber(request.getPhoneNumber());
        event.setTimestamp(System.currentTimeMillis());

        logger.info("Publishing payment success event for order: {} with email: {} and phone: {}", 
                   event.getOrderId(), event.getUserEmail(), event.getPhoneNumber());
        
        try {
            eventProducer.publishPaymentSuccessEvent(event);
        } catch (Exception e) {
            // Log the error but don't crash the response
            logger.error("Failed to publish Kafka event for order {}: {}", 
                        savedPayment.getOrderId(), e.getMessage(), e);
        }

        // Return response
        return new PaymentResponse(
                savedPayment.getPaymentId(),
                savedPayment.getStatus(),
                savedPayment.getOrderId(),
                savedPayment.getAmount()
        );
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getStatus(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getStatus(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }
}
