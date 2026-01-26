# Payment Service - E-commerce Microservice

Payment microservice for processing payments in the e-commerce platform.

## Project Structure

```
payment-service/
├── src/main/java/com/payment/
│   ├── PaymentServiceApplication.java
│   ├── controller/
│   │   └── PaymentController.java
│   ├── service/
│   │   └── PaymentService.java
│   ├── entity/
│   │   └── Payment.java
│   ├── repository/
│   │   └── PaymentRepository.java
│   └── dto/
│       ├── PaymentRequest.java
│       └── PaymentResponse.java
├── src/main/resources/
│   └── application.yml
├── pom.xml
└── README.md
```

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+

## Setup Instructions

### 1. Create Database

```sql
CREATE DATABASE payment_db;
```

### 2. Run the Application

```bash
cd payment-service
mvn clean install
mvn spring-boot:run
```

The service will start on `http://localhost:8083`

## API Endpoints

### 1. Process Payment

**POST** `/api/payments/process`

**Request:**
```json
{
  "orderId": 101,
  "amount": 500,
  "currency": "INR",
  "paymentMethod": "RAZORPAY"
}
```

**Response:**
```json
{
  "paymentId": "PAY12345678",
  "status": "SUCCESS",
  "orderId": 101,
  "amount": 500.0
}
```

### 2. Get Payment by ID

**GET** `/api/payments/{paymentId}`

**Response:**
```json
{
  "paymentId": "PAY12345678",
  "status": "SUCCESS",
  "orderId": 101,
  "amount": 500.0
}
```

### 3. Get Payment by Order ID

**GET** `/api/payments/order/{orderId}`

**Response:**
```json
{
  "paymentId": "PAY12345678",
  "status": "SUCCESS",
  "orderId": 101,
  "amount": 500.0
}
```

### 4. Health Check

**GET** `/api/payments/health`

**Response:**
```
Payment Service is UP
```

## Testing with Postman

1. Create a new POST request: `http://localhost:8083/api/payments/process`
2. Set header: `Content-Type: application/json`
3. Add body:
```json
{
  "orderId": 101,
  "amount": 500,
  "currency": "INR",
  "paymentMethod": "RAZORPAY"
}
```
4. Click Send

## Database

The service uses MySQL for persistence. Database tables are automatically created via Hibernate (ddl-auto: update).

**Payment Table:**
- `id` (Long, PK)
- `order_id` (Long, NOT NULL)
- `amount` (Double, NOT NULL)
- `currency` (String, NOT NULL)
- `payment_method` (String, NOT NULL)
- `status` (String, NOT NULL)
- `payment_id` (String, UNIQUE)
- `created_at` (LocalDateTime)
- `updated_at` (LocalDateTime)

## Future Enhancements

- [ ] Kafka event publishing for payment events
- [ ] Spring Security integration
- [ ] Payment gateway integration (Razorpay, Stripe)
- [ ] Docker containerization
- [ ] Unit and integration tests
- [ ] API documentation with Swagger/OpenAPI
