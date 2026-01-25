package com.payment.controller;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.service.PaymentService;
import com.payment.service.RazorpayPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RazorpayPaymentService razorpayPaymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<Map<String, String>> createRazorpayOrder(
            @RequestParam Long orderId,
            @RequestParam Double amount,
            @RequestParam String currency,
            @RequestParam String email,
            @RequestParam(required = false) String phoneNumber) {
        
        String razorpayOrderId = razorpayPaymentService.createPaymentOrder(
                orderId, amount, currency, email, phoneNumber
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("orderId", razorpayOrderId);
        response.put("keyId", razorpayPaymentService.getKeyId());
        response.put("amount", String.valueOf((long)(amount * 100))); // In paise
        response.put("currency", currency);
        response.put("email", email);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<Map<String, String>> verifyRazorpayPayment(
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature) {
        
        boolean isValid = razorpayPaymentService.verifyPaymentSignature(orderId, paymentId, signature);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", isValid ? "SUCCESS" : "FAILED");
        response.put("paymentId", paymentId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is UP");
    }
}
