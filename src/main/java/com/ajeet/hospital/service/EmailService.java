package com.ajeet.hospital.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    @Value("${resend.from-email:onboarding@resend.dev}")
    private String fromEmail;

    public EmailService(
            @Value("${resend.api-key}") String apiKey) {

        this.resend = new Resend(apiKey);
    }

    public void sendPasswordResetEmail(
            String email,
            String resetLink) {

        String htmlContent =
                """
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>Hospital Management System</h2>

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
                           ">
                            Reset Password
                        </a>
                    </p>

                    <p>
                        Or copy and paste this link into your browser:
                    </p>

                    <p>%s</p>

                    <p>
                        This link will expire in 15 minutes.
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
                """.formatted(resetLink, resetLink);

        CreateEmailOptions emailOptions =
                CreateEmailOptions.builder()
                        .from(fromEmail)
                        .to(email)
                        .subject(
                                "Hospital Management System - Password Reset"
                        )
                        .html(htmlContent)
                        .build();

        try {

            resend.emails().send(emailOptions);

        } catch (ResendException e) {

            throw new RuntimeException(
                    "Failed to send password reset email",
                    e
            );
        }
    }
}