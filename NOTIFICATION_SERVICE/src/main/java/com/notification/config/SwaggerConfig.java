package com.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .version("1.0.0")
                        .description("Notification delivery microservice for e-commerce platform")
                        .contact(new Contact()
                                .name("Notification Service Team")
                                .email("notifications@ecommerce.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8084")
                        .description("Development Server"));
    }
}
