package com.example.telegrambot_ms.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Finance Tracker Bot <myfinancebot@gmail.com>");
        message.setTo(email);
        message.setSubject("Finance Tracker Bot OTP");
        message.setText("Hello, your verification code is: " + otp);

        mailSender.send(message);
    }
}
