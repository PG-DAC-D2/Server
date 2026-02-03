package com.notification.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String message) {
        super(message);
    }
}

class EmailSendException extends RuntimeException {
    public EmailSendException(String message) {
        super(message);
    }
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}

class SmsSendException extends RuntimeException {
    public SmsSendException(String message) {
        super(message);
    }
    public SmsSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
