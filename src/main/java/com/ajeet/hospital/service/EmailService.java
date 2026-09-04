package com.ajeet.hospital.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String email,
            String resetLink) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "Hospital Management System - Password Reset"
        );

        message.setText(
                "Hello,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to create a new password:\n\n" +
                        resetLink +
                        "\n\n" +
                        "This link will expire in 15 minutes.\n\n" +
                        "If you did not request a password reset, " +
                        "please ignore this email.\n\n" +
                        "Regards,\n" +
                        "Hospital Management System"
        );

        mailSender.send(message);
    }
}