package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

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
