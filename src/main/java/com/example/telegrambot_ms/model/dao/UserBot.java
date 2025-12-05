package com.example.telegrambot_ms.model.dao;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.telegrambot_ms.enums.BotState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
@Document(collection = "user_bot")
public class UserBot {
    @Id
    private String id;
    @Indexed(unique = true)
    private Long telegramId;
    private String token;
    private Instant expiresAt;
    private BotState botState;
    private String emailTemp;
    private String emailVerificationCode;
    private Instant emailVerificationCodeExpiresAt;

}
