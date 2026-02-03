package com.payment.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
    private static final String TOPIC = "payment-events";

    // ✅ REQUIRED (not required=false) - Fails fast if Kafka misconfigures
    @Autowired
    private KafkaTemplate<String, PaymentEventDTO> kafkaTemplate;

    public void publishPaymentSuccessEvent(PaymentEventDTO event) {
        try {
            kafkaTemplate.send(TOPIC, event);
            logger.info("Published PAYMENT_SUCCESS event for order: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish payment event", e);
        }
    }

    public void publishPaymentFailureEvent(PaymentEventDTO event) {
        try {
            kafkaTemplate.send(TOPIC, event);
            logger.info("Published PAYMENT_FAILED event for order: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish payment event", e);
        }
    }
}
