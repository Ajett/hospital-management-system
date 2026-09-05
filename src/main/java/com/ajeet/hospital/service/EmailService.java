package com.ajeet.hospital.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.from-email}")
    private String fromEmail;

    @Value("${brevo.from-name:Hospital Management System}")
    private String fromName;

    public EmailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    public void sendPasswordResetEmail(
            String email,
            String resetLink) {

        String htmlContent =
                """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">

                    <h2 style="color: #0d6efd;">
                        Hospital Management System
                    </h2>

                    <p>Hello,</p>

                    <p>
                        We received a request to reset your password.
                    </p>

                    <p>
                        Click the button below to create a new password:
                    </p>

                    <p>
                        <a href="%s"
                           style="
                               display: inline-block;
                               padding: 12px 20px;
                               background-color: #0d6efd;
                               color: white;
                               text-decoration: none;
                               border-radius: 6px;
                               font-weight: bold;
                           ">
                            Reset Password
                        </a>
                    </p>

                    <p>
                        Or copy and paste this link into your browser:
                    </p>

                    <p>
                        <a href="%s">%s</a>
                    </p>

                    <p>
                        <strong>This link will expire in 15 minutes.</strong>
                    </p>

                    <p>
                        If you did not request a password reset,
                        please ignore this email.
                    </p>

                    <p>
                        Regards,<br>
                        Hospital Management System
                    </p>

                </div>
                """.formatted(
                        resetLink,
                        resetLink,
                        resetLink
                );

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "name", fromName,
                        "email", fromEmail
                ),
                "to", new Object[]{
                        Map.of(
                                "email", email
                        )
                },
                "subject",
                "Hospital Management System - Password Reset",
                "htmlContent",
                htmlContent
        );

        try {

            restClient.post()
                    .uri("/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send password reset email",
                    e
            );
        }
    }
}