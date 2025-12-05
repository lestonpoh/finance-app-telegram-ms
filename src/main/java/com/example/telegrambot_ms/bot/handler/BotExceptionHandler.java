package com.example.telegrambot_ms.bot.handler;

import org.springframework.stereotype.Component;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;

@Component
public class BotExceptionHandler {
    public void handle(Exception e, FinanceTrackerBot bot, Long chatId) {
        bot.sendMessage("⚠️ Error occured", chatId);
        e.printStackTrace();
    }
}