# API Gateway – Spring Boot

## Tech Stack
- Spring Boot
- Spring Cloud Gateway
- JWT Authentication

## Microservices
- User Service (Spring Boot)
- Order Service (.NET)
- Product Service (Node.js)

## Run Order
1. Start User Service (8081)
2. Start Order Service (5001)
3. Start Product Service (3000)
4. Start API Gateway (8080)

## Sample Request
```
GET /api/products
Authorization: Bearer <JWT>
```