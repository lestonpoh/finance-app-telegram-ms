package com.example.telegrambot_ms.enums;

public enum BotState {
    REGISTER_AWAITING_EMAIL("registration"),
    REGISTER_AWAITING_CODE("registration"),
    INVESTMENTS_AWAITING_TYPE("investments");

    private final String stateType;

    BotState(String stateType) {
        this.stateType = stateType;
    }

    public String getStateType() {
        return stateType;
    }
}
