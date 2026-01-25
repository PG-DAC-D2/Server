package com.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private final RestTemplate restTemplate;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    public SmsService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public boolean sendSms(String phoneNumber, String message) {
        try {
            logger.info("=== SMS NOTIFICATION SERVICE ===");
            logger.info("Sending SMS to: {}", phoneNumber);
            logger.info("From (Twilio): {}", twilioPhoneNumber);
            logger.info("Message: {}", message);
            logger.info("Account SID: {}", accountSid);
            logger.info("Using Twilio API...");
            
            // Call Twilio REST API directly
            String twilioUrl = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";
            
            // Create request body
            String requestBody = "To=" + urlEncode(phoneNumber) + 
                               "&From=" + urlEncode(twilioPhoneNumber) + 
                               "&Body=" + urlEncode(message);
            
            logger.info("Request URL: {}", twilioUrl);
            logger.info("Request Body: {}", requestBody);
            
            try {
                // Using RestTemplate with Basic Auth
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setBasicAuth(accountSid, authToken);
                headers.set("Content-Type", "application/x-www-form-urlencoded");
                
                org.springframework.http.HttpEntity<String> request = 
                    new org.springframework.http.HttpEntity<>(requestBody, headers);
                
                logger.info("Calling Twilio API with credentials...");
                org.springframework.http.ResponseEntity<String> response = 
                    restTemplate.postForEntity(twilioUrl, request, String.class);
                
                logger.info("Twilio Response Status: {}", response.getStatusCodeValue());
                logger.info("Twilio Response Body: {}", response.getBody());
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("SMS SENT SUCCESSFULLY to {}", phoneNumber);
                    logger.info("Status Code: {}", response.getStatusCodeValue());
                    logger.info("================================");
                    return true;
                } else {
                    logger.error("Twilio API returned status: {}", response.getStatusCodeValue());
                    logger.error("Response: {}", response.getBody());
                    return false;
                }
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                logger.error("Twilio HTTP Error: Status {}", e.getStatusCode());
                logger.error("Error Message: {}", e.getMessage());
                logger.error("Response Body: {}", e.getResponseBodyAsString());
                return false;
            } catch (org.springframework.web.client.ResourceAccessException e) {
                logger.error("Network Error connecting to Twilio: {}", e.getMessage());
                return false;
            } catch (Exception e) {
                logger.error("Exception while calling Twilio API: {}", e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return value;
        }
    }

    public boolean sendPaymentSuccessSms(String phoneNumber, Long orderId, Double amount, String currency) {
        String message = "Your payment of " + amount + " " + currency + " for Order #" + orderId + 
                        " has been processed successfully. Thank you!";
        return sendSms(phoneNumber, message);
    }

    public boolean sendPaymentFailureSms(String phoneNumber, Long orderId) {
        String message = "Payment failed for Order #" + orderId + ". Please try again or contact support.";
        return sendSms(phoneNumber, message);
    }
}
