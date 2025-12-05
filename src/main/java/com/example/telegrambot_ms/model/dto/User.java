package com.example.telegrambot_ms.model.dto;

import lombok.Getter;

@Getter
public class User {
    private String email;
    private String username;
    private Long telegramId;
    private String token;
}
