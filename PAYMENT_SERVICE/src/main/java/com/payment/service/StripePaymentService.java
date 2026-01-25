package com.payment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StripePaymentService {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentService.class);

    @Value("${stripe.api-key:}")
    private String apiKey;

    public boolean processStripePayment(Long orderId, Double amount, String currency, String email) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("Stripe API key not configured. Simulating payment");
                return true;
            }

            Stripe.apiKey = apiKey;

            // Create Payment Intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100))  // Stripe uses cents
                    .setCurrency(currency.toLowerCase())
                    .setReceiptEmail(email)
                    .putMetadata("orderId", orderId.toString())
                    .setDescription("Order #" + orderId)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            
            logger.info("Stripe PaymentIntent created: {} for Order: {}", intent.getId(), orderId);
            return intent.getStatus().equals("succeeded") || intent.getStatus().equals("requires_payment_method");

        } catch (StripeException e) {
            logger.error("Stripe payment error for Order {}: {}", orderId, e.getMessage());
            return false;
        }
    }

    public PaymentIntent retrievePaymentIntent(String clientSecret) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("Stripe API key not configured");
                return null;
            }

            Stripe.apiKey = apiKey;
            String intentId = clientSecret.split("_secret_")[0];
            return PaymentIntent.retrieve(intentId);

        } catch (StripeException e) {
            logger.error("Error retrieving payment intent: {}", e.getMessage());
            return null;
        }
    }
}
