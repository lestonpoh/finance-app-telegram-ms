package com.example.telegrambot_ms.bot.handler;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;
import com.example.telegrambot_ms.enums.BotState;
import com.example.telegrambot_ms.model.dao.UserBot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CallbackHandler {
    private final InvestmentHandler investmentHandler;

    public void handleCallback(long chatId, String callback, UserBot userBot, FinanceTrackerBot bot) {
        BotState botState = Optional.ofNullable(userBot)
                .map(UserBot::getBotState)
                .orElse(null);
        // handle callback based on bot state
        if (botState == null) {
            bot.sendMessage(
                    "I do not understand what you mean, start a flow using a command",
                    chatId);
        } else if (botState.getStateType() == "investments") {
            investmentHandler.handleInvestment(callback, chatId, userBot, bot);
        }
    }
}
