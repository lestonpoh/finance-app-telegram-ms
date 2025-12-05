package com.example.telegrambot_ms.bot.handler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;
import com.example.telegrambot_ms.client.BackendClient;
import com.example.telegrambot_ms.enums.BotState;
import com.example.telegrambot_ms.model.dao.UserBot;
import com.example.telegrambot_ms.model.dto.User;
import com.example.telegrambot_ms.repository.UserBotRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final UserBotRepository userBotRepository;
    private final BackendClient backendClient;

    public void handleCommand(Message message, UserBot userBot, FinanceTrackerBot bot) throws TelegramApiException {
        String command = message.getText();
        long chatId = message.getChatId();

        if (command.equals("/start")) {

            User user = backendClient.getUserByTelegramId(chatId);
            if (user != null) {
                userBot.setToken(user.getToken());
                userBot.setExpiresAt(Instant.now().plus(Duration.ofMinutes(60)));
                userBotRepository.save(userBot);
            } else {
                bot.sendMessage(
                        "Telegram account not linked, type /register to start registration process.",
                        chatId);
                userBot.setExpiresAt(Instant.now().plus(Duration.ofMinutes(60)));
                userBotRepository.save(userBot);
            }
            return;
        }
        if (command.equals("/register")) {
            userBot.setBotState(BotState.REGISTER_AWAITING_EMAIL);
            userBotRepository.save(userBot);
            bot.sendMessage("Registration started. Please enter your email", chatId);
            return;
        }

        // check if user is registered
        if (userBot.getToken() == null) {
            bot.sendMessage(
                    "Telegram account not linked, type /register to start registration process.",
                    chatId);

        }

        switch (command) {
            case "/investments":
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("Register");
                btn1.setCallbackData("REGISTER");

                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("Cancel");
                btn2.setCallbackData("CANCEL");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(
                        List.of(
                                List.of(btn1, btn2)));

                SendMessage message1 = new SendMessage();
                message1.setChatId(chatId);
                message1.setText("Choose an option:");
                message1.setReplyMarkup(markup);

                bot.execute(message1);
                // userBotRepository.save(UserBot.builder()
                // .id(userBot.getId())
                // .telegramId(userBot.getTelegramId())
                // .botState(BotState.INVESTMENTS_AWAITING_TYPE)
                // .build());
                // bot.sendMessage("Which investment account do you want to view",
                // chatId);
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
