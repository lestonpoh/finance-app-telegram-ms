package com.example.telegrambot_ms.bot.handler;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;
import com.example.telegrambot_ms.enums.BotState;
import com.example.telegrambot_ms.model.dao.UserBot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TextHandler {
    private final RegistrationHandler registerHandler;

    public void handleText(Message message, UserBot userBot, FinanceTrackerBot bot) {
        String text = message.getText();
        long chatId = message.getChatId();

        BotState botState = Optional.ofNullable(userBot)
                .map(UserBot::getBotState)
                .orElse(null);

        // check if user exists/ is in midst of registration, else prompt registration
        if (userBot.getToken() == null && botState == null) {
            bot.sendMessage(
                    "Telegram account not linked, type /register to start registration process.",
                    chatId);
            return;
        }
        // handle text based on bot state
        if (botState == null) {
            bot.sendMessage(
                    "I do not understand what you mean, start a flow using a command",
                    chatId);
        } else if (botState.getStateType() == "registration") {
            registerHandler.handleRegister(text, chatId, userBot, bot);
        }
    }
}
