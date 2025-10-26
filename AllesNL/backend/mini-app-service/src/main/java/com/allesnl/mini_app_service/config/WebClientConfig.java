package com.allesnl.mini_app_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient paymentServiceClient() {
        return WebClient.builder()
                .baseUrl("http://payment-service:8080")
                .build();
    }

    @Bean
    public WebClient APIGatewayClient() {
        return WebClient.builder()
                .baseUrl("http://api-gateway:8080")
                .build();
    }

    @Bean
    public WebClient userServiceClient() {
        return WebClient.builder()
                .baseUrl("http://user-service:8080")
                .build();
    }
}