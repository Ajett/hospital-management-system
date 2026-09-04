package com.ajeet.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(
            min = 6,
            max = 100,
            message = "Password must contain at least 6 characters"
    )
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}