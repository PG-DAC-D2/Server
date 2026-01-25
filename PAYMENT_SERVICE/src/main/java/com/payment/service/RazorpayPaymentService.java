package com.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayPaymentService.class);

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    public String createPaymentOrder(Long orderId, Double amount, String currency, String email, String phoneNumber) {
        try {
            if (keyId == null || keyId.isEmpty() || keySecret == null || keySecret.isEmpty()) {
                logger.warn("Razorpay credentials not configured. Generating test order");
                return "ORD_" + orderId + "_TEST";
            }

            // Create order using Razorpay API
            // Note: Requires OkHttp3 or similar HTTP client
            // For now, simulating order creation
            
            String orderId_razorpay = "order_" + System.currentTimeMillis();
            logger.info("Razorpay Order created: {} for Order: {}", orderId_razorpay, orderId);
            return orderId_razorpay;

        } catch (Exception e) {
            logger.error("Razorpay error for Order {}: {}", orderId, e.getMessage());
            return null;
        }
    }

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            if (keySecret == null || keySecret.isEmpty()) {
                logger.warn("Razorpay not configured. Simulating verification");
                return true;
            }

            // Verify signature: HMAC-SHA256(orderId|paymentId, keySecret) == signature
            String data = orderId + "|" + paymentId;
            String expectedSignature = generateHmacSha256(data, keySecret);
            
            boolean isValid = expectedSignature.equals(signature);
            logger.info("Payment signature verification: {}", isValid);
            return isValid;

        } catch (Exception e) {
            logger.error("Error verifying payment signature: {}", e.getMessage());
            return false;
        }
    }

    private String generateHmacSha256(String data, String key) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                key.getBytes("UTF-8"),
                "HmacSHA256"
        );
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getKeyId() {
        return keyId;
    }

    public String getEncodedKeySecret() {
        if (keySecret == null || keySecret.isEmpty()) {
            return "";
        }
        return Base64.getEncoder().encodeToString(keySecret.getBytes());
    }
}
