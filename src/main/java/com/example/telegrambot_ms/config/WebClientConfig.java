package com.example.telegrambot_ms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.backend}")
    private String urlPrefix;

    @Value("${internal.secret}")
    private String internalSecret;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(urlPrefix)
                .defaultHeader("X-INTERNAL-AUTH", internalSecret)
                .build();
    }
}
