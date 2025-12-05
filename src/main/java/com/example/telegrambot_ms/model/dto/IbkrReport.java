package com.example.telegrambot_ms.model.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class IbkrReport {
    private float totalAssetValueSGD;
    private float totalPostionValueSGD;
    private float totalCashSgd;
    private List<Cash> cashList;
    private List<Position> positionList;

    @Getter
    public static class Cash {
        private String currency;
        private float value;
    }

    @Getter
    public static class Position {
        private String currency;
        private String symbol;
        private String description;
        private float position;
        private float positionValue;
        private float positionValueSGD;
        private float costPrice;
        private float currentPrice;
        private float unrealizedGains;
        private float unrealizedGainsPercent;
    }
}