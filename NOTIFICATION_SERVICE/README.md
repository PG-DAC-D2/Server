# Notification Service - E-commerce Microservice

Notification microservice for handling Email/SMS notifications and consuming Kafka events from Payment Service.

## Project Structure

```
notification-service/
├── src/main/java/com/notification/
│   ├── NotificationServiceApplication.java
│   ├── controller/
│   │   └── NotificationController.java
│   ├── service/
│   │   ├── NotificationService.java
│   │   └── EmailService.java
│   ├── entity/
│   │   └── Notification.java
│   ├── repository/
│   │   └── NotificationRepository.java
│   ├── kafka/
│   │   └── PaymentEventConsumer.java
│   └── dto/
│       ├── NotificationRequest.java
│       └── PaymentEventMessage.java
├── src/main/resources/
│   └── application.yml
├── pom.xml
└── README.md
```

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Apache Kafka (optional for event-driven features)

## Setup Instructions

### 1. Create Database

```sql
CREATE DATABASE notification_db;
```

### 2. Run the Application

```bash
cd notification-service
mvn clean install
java -jar target/notification-service-1.0.0.jar
```

The service will start on `http://localhost:8084`

## API Endpoints

### 1. Send Notification

**POST** `/api/notifications/send`

**Request:**
```json
{
  "recipientEmail": "user@example.com",
  "phoneNumber": "+91-9876543210",
  "subject": "Order Confirmation",
  "message": "Your order has been confirmed",
  "notificationType": "EMAIL",
  "orderId": 101
}
```

**Response:**
```json
{
  "id": 1,
  "orderId": 101,
  "recipientEmail": "user@example.com",
  "subject": "Order Confirmation",
  "message": "Your order has been confirmed",
  "notificationType": "EMAIL",
  "status": "SENT",
  "createdAt": "2026-01-25T22:30:00",
  "sentAt": "2026-01-25T22:30:05"
}
```

### 2. Get Notifications by Order ID

**GET** `/api/notifications/order/{orderId}`

**Response:**
```json
[
  {
    "id": 1,
    "orderId": 101,
    "recipientEmail": "user@example.com",
    "status": "SENT",
    "notificationType": "EMAIL"
  }
]
```

### 3. Get Notifications by Email

**GET** `/api/notifications/email/{email}`

### 4. Get Pending Notifications

**GET** `/api/notifications/pending`

Returns all notifications with status "PENDING"

### 5. Get Failed Notifications

**GET** `/api/notifications/failed`

Returns all notifications with status "FAILED"

### 6. Health Check

**GET** `/api/notifications/health`

**Response:**
```
Notification Service is UP
```

## Kafka Event Integration

The service listens to `payment-events` topic and automatically sends notifications when:

- **PAYMENT_SUCCESS**: Sends success email to user
- **PAYMENT_FAILED**: Sends failure email to user

### Payment Event Format

```json
{
  "eventType": "PAYMENT_SUCCESS",
  "orderId": 101,
  "paymentId": "PAY12345678",
  "amount": 500.0,
  "currency": "INR",
  "paymentMethod": "RAZORPAY",
  "userEmail": "user@example.com",
  "userName": "John Doe",
  "timestamp": 1674686400000
}
```

## Database

The service uses MySQL for persistence. Database tables are automatically created via Hibernate.

**Notification Table:**
- `id` (Long, PK)
- `order_id` (Long, NOT NULL)
- `recipient_email` (String, NOT NULL)
- `phone_number` (String)
- `subject` (String, NOT NULL)
- `message` (LONGTEXT, NOT NULL)
- `notification_type` (String, NOT NULL) - EMAIL, SMS
- `status` (String, NOT NULL) - PENDING, SENT, FAILED
- `created_at` (LocalDateTime)
- `updated_at` (LocalDateTime)
- `sent_at` (LocalDateTime)

## Configuration

### Email Configuration (Gmail Example)

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password  # Use Gmail App Password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

For Gmail, create an [App Password](https://support.google.com/accounts/answer/185833)

### Kafka Configuration

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service
```

## Testing with Postman

### Send Notification
- **Method:** POST
- **URL:** `http://localhost:8084/api/notifications/send`
- **Body:**
```json
{
  "recipientEmail": "test@example.com",
  "phoneNumber": "+91-9876543210",
  "subject": "Test Notification",
  "message": "This is a test notification",
  "notificationType": "EMAIL",
  "orderId": 101
}
```

## Microservice Communication

### Payment Service → Notification Service Flow

1. **Payment Service** processes payment
2. **Payment Service** publishes `PAYMENT_SUCCESS` event to Kafka topic `payment-events`
3. **Notification Service** listens on Kafka topic
4. **Notification Service** receives event and sends email notification
5. **Notification Service** saves notification record to database

```
Payment Service  →  Kafka  →  Notification Service  →  Email/SMS
   (Event)              (Topic)      (Consumer)        (User)
```

## Future Enhancements

- [ ] SMS integration (Twilio, AWS SNS)
- [ ] WhatsApp notifications
- [ ] Push notifications
- [ ] Notification templates
- [ ] Retry mechanism for failed notifications
- [ ] Notification preferences per user
- [ ] Docker containerization
- [ ] Unit and integration tests
