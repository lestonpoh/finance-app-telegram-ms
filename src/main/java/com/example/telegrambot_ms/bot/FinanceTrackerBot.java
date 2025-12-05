package com.example.telegrambot_ms.bot;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.telegrambot_ms.bot.handler.BotExceptionHandler;
import com.example.telegrambot_ms.bot.handler.CommandHandler;
import com.example.telegrambot_ms.bot.handler.TextHandler;
import com.example.telegrambot_ms.model.dao.UserBot;
import com.example.telegrambot_ms.repository.UserBotRepository;

@Component
public class FinanceTrackerBot extends TelegramLongPollingBot {

    private final CommandHandler commandHandler;
    private final TextHandler textHandler;
    private final BotExceptionHandler exceptionHandler;
    private final UserBotRepository userBotRepository;
    @Value("${telegram.bot.username}")
    private String botUsername;

    public FinanceTrackerBot(
            @Value(value = "${telegram.bot.token}") String botToken,
            TextHandler textHandler,
            CommandHandler commandHandler,
            BotExceptionHandler exceptionHandler,
            UserBotRepository userBotRepository) {
        super(botToken);
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
        this.exceptionHandler = exceptionHandler;
        this.userBotRepository = userBotRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            // check if bot started in the past hour
            UserBot userBot = userBotRepository.findByTelegramId(message.getChatId())
                    .orElse(UserBot.builder()
                            .telegramId(message.getChatId())
                            .build());

            // session has not expired
            if (message.getText().equals("/start")
                    || (userBot.getExpiresAt() != null && Instant.now().isBefore(userBot.getExpiresAt()))) {
                if (message.isCommand()) {
                    commandHandler.handleCommand(message, userBot, this);
                    return;
                }

                if (message.hasText()) {
                    textHandler.handleText(message, userBot, this);
                    return;
                }
            } else {
                sendMessage(
                        "Type /start to begin session",
                        message.getChatId());
            }

        } catch (Exception e) {
            exceptionHandler.handle(e, this, update.getMessage().getChatId());
        }

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMessage(String message, Long chatId) {
        SendMessage newMessage = new SendMessage();
        newMessage.setChatId(chatId);
        newMessage.setText(message);
        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
