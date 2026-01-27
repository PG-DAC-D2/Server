package com.ecommerce.exceptions;

import org.springframework.web.bind.annotation.*;

import com.ecommerce.models.AppResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public AppResponse<?> handleNotFound(ResourceNotFoundException ex) {
        return AppResponse.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AppResponse<?> handleGeneric(Exception ex) {
        return AppResponse.error("Something went wrong");
    }
}