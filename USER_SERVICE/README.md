# User Service

This is the User Service for the E-commerce microservice architecture.

## Features

- User authentication and authorization with JWT
- Registration for Customers and Merchants
- Admin can deactivate Merchants
- Roles: CUSTOMER, MERCHANT, ADMIN

## Running the Application

1. Ensure Java 18 and MySQL are installed.
2. Set up a MySQL database named 'user_service' (or update the URL in application.properties).
3. Update the database credentials in application.properties.
4. Run `mvn spring-boot:run`

## APIs

- POST /authenticate - Login
- POST /register/customer - Register Customer
- POST /register/merchant - Register Merchant
- DELETE /admin/merchant/{id} - Deactivate Merchant (Admin only)
