package com.example.telegrambot_ms.bot.handler;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;
import com.example.telegrambot_ms.client.BackendClient;
import com.example.telegrambot_ms.enums.BotState;
import com.example.telegrambot_ms.model.dao.UserBot;
import com.example.telegrambot_ms.model.dto.User;
import com.example.telegrambot_ms.repository.UserBotRepository;
import com.example.telegrambot_ms.service.EmailService;
import com.example.telegrambot_ms.util.EmailValidator;
import com.example.telegrambot_ms.util.OtpGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegistrationHandler {
    private final UserBotRepository userBotRepository;
    private final BackendClient backendClient;
    private final EmailService emailService;

    public void handleRegister(String text, long chatId, UserBot userBot, FinanceTrackerBot bot) {
        switch (userBot.getBotState()) {
            case BotState.REGISTER_AWAITING_EMAIL:
                if (EmailValidator.validate(text)) {
                    userBot.setBotState(BotState.REGISTER_AWAITING_CODE);
                    userBot.setEmailTemp(text);
                    String otp = OtpGenerator.generate();
                    userBot.setEmailVerificationCode(otp);
                    // userBot.setEmailVerificationCodeExpiresAt(Instant.now().plus(Duration.ofMinutes(5)));
                    userBotRepository.save(userBot);
                    bot.sendMessage(
                            String.format(
                                    "Verification code sent to: %s. Please enter code. Code expires in 5 minutes.",
                                    text),
                            chatId);
                    emailService.sendOtpEmail(text, otp);
                } else {
                    bot.sendMessage("Invalid email format. Enter another email.", chatId);
                }
                break;
            case BotState.REGISTER_AWAITING_CODE:
                if (text.equals(userBot.getEmailVerificationCode())) {
                    backendClient.updateUserTelegramId(userBot.getEmailTemp(), chatId);
                    // get token from updated user
                    User user = backendClient.getUserByTelegramId(chatId);
                    if (user != null) {
                        bot.sendMessage("Account successfully registered", chatId);
                        userBot.setToken(user.getToken());
                        userBot.setExpiresAt(Instant.now().plus(Duration.ofMinutes(60)));
                        userBot.setBotState(null);
                        userBot.setEmailTemp(null);
                        userBot.setEmailVerificationCode(null);
                        userBotRepository.save(userBot);
                    } else {
                        throw new RuntimeException("Failed to update TelegramId");
                    }
                } else {
                    bot.sendMessage("Invalid code or code has expired. Try again", chatId);
                }

                break;
            // this should not be exposed, only for debugging
            default:
                bot.sendMessage("Invalid registration flow", chatId);
                break;
        }
    }
}