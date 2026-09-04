package com.ajeet.hospital.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host:localhost}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender =
                new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);

        if (!username.isBlank()) {
            mailSender.setUsername(username);
        }

        if (!password.isBlank()) {
            mailSender.setPassword(password);
        }

        Properties properties =
                mailSender.getJavaMailProperties();

        properties.put(
                "mail.smtp.auth",
                "true"
        );

        properties.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        return mailSender;
    }
}