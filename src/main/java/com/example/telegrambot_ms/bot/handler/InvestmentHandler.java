package com.example.telegrambot_ms.bot.handler;

import org.springframework.stereotype.Component;

import com.example.telegrambot_ms.bot.FinanceTrackerBot;
import com.example.telegrambot_ms.client.BackendClient;
import com.example.telegrambot_ms.enums.BotState;
import com.example.telegrambot_ms.model.dao.UserBot;
import com.example.telegrambot_ms.model.dto.IbkrReport;
import com.example.telegrambot_ms.model.dto.IbkrReport.Cash;
import com.example.telegrambot_ms.model.dto.IbkrReport.Position;
import com.example.telegrambot_ms.repository.UserBotRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvestmentHandler {
    private final BackendClient backendClient;
    private final UserBotRepository userBotRepository;

    public void handleInvestment(String text, long chatId, UserBot userBot, FinanceTrackerBot bot) {
        if (userBot.getBotState() == BotState.INVESTMENTS_AWAITING_TYPE) {
            switch (text) {
                case "IBKR":
                    IbkrReport ibkrReport = backendClient.getIbkrInfo(userBot.getToken());

                    StringBuilder sb = new StringBuilder();
                    sb.append("<pre>");
                    sb.append(
                            String.format("%-21s: SGD %s%n", "Total Asset Value", ibkrReport.getTotalAssetValueSGD()));
                    sb.append(
                            String.format("%-21s: SGD %s%n", "Total Position Value",
                                    ibkrReport.getTotalPostionValueSGD()));
                    sb.append(String.format("%-21s: SGD %s%n", "Total Cash", ibkrReport.getTotalCashSgd()));
                    sb.append("\n");
                    sb.append("Cash\n");
                    sb.append("===========================\n");
                    for (Cash cash : ibkrReport.getCashList()) {
                        sb.append(String.format("%-4s %s\n", cash.getCurrency(), cash.getValue()));
                    }
                    sb.append("\n");
                    sb.append("Positions\n");
                    sb.append("===========================\n");
                    sb.append(String.format("%-9s %-9s %-9s %-9s %-9s %s\n", "Symbol",
                            "Position", "Value", "Cost", "Current", "Gain"));
                    for (Position position : ibkrReport.getPositionList()) {
                        sb.append(String.format("%-9s %-9s %-9s %-9s %-9s %-7s (%s%%)\n",
                                position.getSymbol(), position.getPosition(), position.getPositionValue(),
                                position.getCostPrice(), position.getCurrentPrice(), position.getUnrealizedGains(),
                                position.getUnrealizedGainsPercent()));
                    }
                    sb.append("</pre>");

                    bot.sendHtmlMessage(sb.toString(), chatId);

                    userBot.setBotState(null);
                    userBotRepository.save(userBot);
                    break;

                default:
                    bot.sendMessage("Invalid account", chatId);
                    break;
            }
        } else {
            // this should not be exposed, only for debugging
            bot.sendMessage("Invalid registration flow", chatId);
        }
    }
}
