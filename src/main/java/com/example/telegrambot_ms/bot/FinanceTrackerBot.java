package com.example.telegrambot_ms.bot;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.telegrambot_ms.bot.handler.BotExceptionHandler;
import com.example.telegrambot_ms.bot.handler.CallbackHandler;
import com.example.telegrambot_ms.bot.handler.CommandHandler;
import com.example.telegrambot_ms.bot.handler.TextHandler;
import com.example.telegrambot_ms.client.BackendClient;
import com.example.telegrambot_ms.model.dao.UserBot;
import com.example.telegrambot_ms.model.dto.User;
import com.example.telegrambot_ms.repository.UserBotRepository;

@Component
public class FinanceTrackerBot extends TelegramLongPollingBot {

    private final CommandHandler commandHandler;
    private final TextHandler textHandler;
    private final CallbackHandler callbackHandler;
    private final BotExceptionHandler exceptionHandler;
    private final UserBotRepository userBotRepository;
    private final BackendClient backendClient;
    @Value("${telegram.bot.username}")
    private String botUsername;

    public FinanceTrackerBot(
            @Value(value = "${telegram.bot.token}") String botToken,
            TextHandler textHandler,
            CommandHandler commandHandler,
            CallbackHandler callbackHandler,
            BotExceptionHandler exceptionHandler,
            UserBotRepository userBotRepository,
            BackendClient backendClient) {
        super(botToken);
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
        this.exceptionHandler = exceptionHandler;
        this.userBotRepository = userBotRepository;
        this.backendClient = backendClient;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            // get chat id
            long chatId;
            Message message = null;
            CallbackQuery callbackQuery = null;
            if (update.hasCallbackQuery()) {
                callbackQuery = update.getCallbackQuery();
                chatId = callbackQuery.getMessage().getChatId();
            } else if (update.hasMessage()) {
                message = update.getMessage();
                chatId = message.getChatId();
            } else {
                return;
            }

            // check if bot started in the past hour
            UserBot userBot = userBotRepository.findByTelegramId(chatId)
                    .orElse(UserBot.builder()
                            .telegramId(chatId)
                            .build());
            if ((userBot.getBotState() != null && userBot.getBotState().getStateType() == "registration"
                    && message != null && !message.isCommand())
                    || (message != null && message.getText().equals("/register"))) {
                // if in registration state, dont have to fetch user info
                // check if bot started in the past hour if not refetch token
            } else if (userBot.getExpiresAt() == null || Instant.now().isAfter(userBot.getExpiresAt())) {
                User user = backendClient.getUserByTelegramId(chatId);
                if (user != null) {
                    userBot.setToken(user.getToken());
                    userBot.setExpiresAt(Instant.now().plus(Duration.ofMinutes(60)));
                    userBotRepository.save(userBot);
                } else {
                    sendMessage(
                            "Telegram account not linked, type /register to start registration process.",
                            chatId);
                    return;
                }
            }

            if (callbackQuery != null) {
                String callBackData = update.getCallbackQuery().getData();
                callbackHandler.handleCallback(chatId, callBackData, userBot, this);
            } else if (message != null) {
                if (message.isCommand()) {
                    commandHandler.handleCommand(chatId, message.getText(), userBot, this);
                    return;
                }
                if (message.hasText()) {
                    textHandler.handleText(chatId, message.getText(), userBot, this);
                    return;
                }
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

    public void sendHtmlMessage(String message, Long chatId) {
        SendMessage newMessage = new SendMessage();
        newMessage.setChatId(chatId);
        newMessage.setText(message);
        newMessage.setParseMode(ParseMode.HTML);
        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendOptions(List<String> options, String question, Long chatId) {
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        for (String option : options) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(option);
            btn.setCallbackData(option);
            buttonList.add(btn);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(buttonList));

        SendMessage newMessage = new SendMessage();
        newMessage.setChatId(chatId);
        newMessage.setText(question);
        newMessage.setReplyMarkup(markup);
        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
