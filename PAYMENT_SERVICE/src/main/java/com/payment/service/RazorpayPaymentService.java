package com.payment.service;

import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import org.json.JSONObject;
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

    // ✅ FIX: Use official Razorpay API client to create orders
    public String createPaymentOrder(Long orderId, Double amount, String currency, String email, String phoneNumber) {
        try {
            if (keyId == null || keyId.isEmpty() || keySecret == null || keySecret.isEmpty()) {
                logger.warn("⚠️ Razorpay credentials not configured");
                throw new RuntimeException("Razorpay credentials missing. Configure razorpay.key-id and razorpay.key-secret");
            }

            // ✅ Create Razorpay client with official SDK
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            // ✅ Prepare order options (OFFICIAL RAZORPAY FORMAT)
            JSONObject options = new JSONObject();
            options.put("amount", (long)(amount * 100));    // Convert to paise (₹5000 = 500000 paise)
            options.put("currency", currency);              // INR
            options.put("receipt", "order_" + orderId);     // Your order reference
            options.put("description", "Payment for order #" + orderId);

            // ✅ Call official Razorpay API to create order
            Order razorpayOrder = client.orders.create(options);

            // ✅ Extract Razorpay order ID from response (e.g., order_K123abc4xyz)
            String razorpayOrderId = razorpayOrder.get("id").toString();
            
            logger.info("✅ Official Razorpay Order created: {} for order #{} (amount: {} {})", 
                       razorpayOrderId, orderId, amount, currency);
            
            return razorpayOrderId;

        } catch (Exception e) {
            logger.error("❌ Failed to create Razorpay order for {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage(), e);
        }
    }

    // ✅ OFFICIAL VERIFICATION: HMAC-SHA256(order_id|payment_id, key_secret) == signature
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            if (keySecret == null || keySecret.isEmpty()) {
                logger.warn("⚠️ Razorpay secret not configured");
                return false;
            }

            // ✅ Official Razorpay verification format: orderId|paymentId
            String data = orderId + "|" + paymentId;
            String expectedSignature = generateHmacSha256(data, keySecret);
            
            boolean isValid = expectedSignature.equals(signature);
            
            if (isValid) {
                logger.info("✅ Payment signature verified for order: {} payment: {}", orderId, paymentId);
            } else {
                logger.error("❌ Invalid signature for order: {}. Received: {}, Expected: {}", 
                    orderId, signature, expectedSignature);
            }
            
            return isValid;

        } catch (Exception e) {
            logger.error("❌ Error verifying payment signature: {}", e.getMessage(), e);
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

    // ✅ For frontend validation only (key secret is NEVER exposed)
    public boolean isConfigured() {
        return keyId != null && !keyId.isEmpty() && 
               keySecret != null && !keySecret.isEmpty();
    }
}
