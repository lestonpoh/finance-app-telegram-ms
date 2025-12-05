package com.example.telegrambot_ms.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        return String.valueOf(random.nextInt(900000) + 100000);
    }
}
