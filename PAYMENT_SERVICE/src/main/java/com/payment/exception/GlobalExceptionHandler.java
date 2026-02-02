package com.payment.exception;

/**
 * Global exception handler for Payment Service
 * Converts exceptions to user-friendly error responses
 */

import com.payment.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors (Invalid input parameters)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("Validation failed: {}", errors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status("VALIDATION_ERROR")
                        .message("Input validation failed")
                        .errors(errors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Handle payment not found
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(
            PaymentNotFoundException ex) {
        
        logger.warn("Payment not found: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status("PAYMENT_NOT_FOUND")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Handle payment processing errors
     */
    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(
            PaymentProcessingException ex) {
        
        logger.error("Payment processing failed: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status("PAYMENT_PROCESSING_ERROR")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Handle Razorpay API errors
     */
    @ExceptionHandler(RazorpayException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ResponseEntity<ErrorResponse> handleRazorpayException(
            RazorpayException ex) {
        
        logger.error("Razorpay API error: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .status("RAZORPAY_ERROR")
                        .message("Payment gateway error. Please try again later.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Handle Kafka errors
     */
    @ExceptionHandler(KafkaPublishException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleKafkaException(
            KafkaPublishException ex) {
        
        logger.error("Kafka publish error: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status("KAFKA_ERROR")
                        .message("Failed to publish event. Please contact support.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Handle database errors
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            org.springframework.dao.DataAccessException ex) {
        
        logger.error("Database error: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status("DATABASE_ERROR")
                        .message("Database operation failed. Please contact support.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex) {
        
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status("INTERNAL_SERVER_ERROR")
                        .message("An unexpected error occurred. Please contact support.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
