package com.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * Generates API documentation automatically
 * Access at: http://localhost:8083/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .version("1.0.0")
                        .description("Payment processing microservice for e-commerce platform")
                        .contact(new Contact()
                                .name("Payment Service Team")
                                .email("payment@ecommerce.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8083")
                        .description("Development Server"))
                .addServersItem(new Server()
                        .url("http://api.ecommerce.com")
                        .description("Production Server"));
    }
}
