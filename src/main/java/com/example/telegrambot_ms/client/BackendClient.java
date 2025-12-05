package com.example.telegrambot_ms.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.telegrambot_ms.model.dto.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackendClient {
    private final WebClient webClient;

    public User getUserByTelegramId(Long telegramId) {
        try {
            User user = webClient.get()
                    .uri("/api/v1/user/telegramId/{id}", telegramId)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
            return user;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND
                    && e.getResponseBodyAsString().equals("No user found with this telegram account")) {
                return null;
            }
            throw e;
        }
    }

    public void updateUserTelegramId(String email, Long telegramId) {
        webClient.patch()
                .uri("/api/v1/user/telegramId?email={email}&telegramId={telegramId}",
                        email, telegramId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
