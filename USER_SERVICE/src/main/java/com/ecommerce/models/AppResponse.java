package com.ecommerce.models;

public class AppResponse<T> {
    private String status;
    private T data;
    private String message;

    public AppResponse(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> AppResponse<T> success(T data) {
        return new AppResponse<>("success", data, null);
    }

    public static <T> AppResponse<T> error(String msg) {
        return new AppResponse<>("error", null, msg);
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}