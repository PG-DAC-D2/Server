package com.payment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
    private static final String TOPIC = "payment-events";

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishPaymentSuccessEvent(PaymentEventDTO event) {
        try {
            if (kafkaTemplate == null) {
                logger.warn("Kafka not configured. Skipping event publication");
                return;
            }

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            logger.info("Published PAYMENT_SUCCESS event for order: {}", event.getOrderId());

        } catch (Exception e) {
            logger.error("Failed to publish payment event", e);
        }
    }

    public void publishPaymentFailureEvent(PaymentEventDTO event) {
        try {
            if (kafkaTemplate == null) {
                logger.warn("Kafka not configured. Skipping event publication");
                return;
            }

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            logger.info("Published PAYMENT_FAILED event for order: {}", event.getOrderId());

        } catch (Exception e) {
            logger.error("Failed to publish payment event", e);
        }
    }
}
