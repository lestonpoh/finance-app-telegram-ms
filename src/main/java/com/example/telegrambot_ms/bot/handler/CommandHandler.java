package com.example.telegrambot_ms.bot.handler;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;
import com.example.telegrambot_ms.enums.BotState;
import com.example.telegrambot_ms.model.dao.UserBot;
import com.example.telegrambot_ms.repository.UserBotRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final UserBotRepository userBotRepository;

    public void handleCommand(long chatId, String command, UserBot userBot, FinanceTrackerBot bot)
            throws TelegramApiException {
        switch (command) {
            case "/register":
                userBot.setBotState(BotState.REGISTER_AWAITING_EMAIL);
                userBotRepository.save(userBot);
                bot.sendMessage("Registration started. Please enter your email", chatId);
                break;

            case "/investments":
                List<String> options = List.of("IBKR");
                bot.sendOptions(options, "Which investment account do you want to view:", chatId);
                userBot.setBotState(BotState.INVESTMENTS_AWAITING_TYPE);
                userBotRepository.save(userBot);
                break;

            default:
                bot.sendMessage("Unknown command", chatId);
                // reset bot state
                userBot.setBotState(null);
                userBotRepository.save(userBot);
                break;
        }

    }
}