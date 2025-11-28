package com.example.telegrambot_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;

@Configuration
public class BotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public FinanceTrackerBot registerBot(TelegramBotsApi telegramBotsApi,
            FinanceTrackerBot bot) throws Exception {
        telegramBotsApi.registerBot(bot);
        return bot;
    }
}
