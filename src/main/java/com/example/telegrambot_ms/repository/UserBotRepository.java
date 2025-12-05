package com.example.telegrambot_ms.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.telegrambot_ms.model.dao.UserBot;

public interface UserBotRepository extends MongoRepository<UserBot, String> {
    Optional<UserBot> findByTelegramId(Long telegramId);
}
