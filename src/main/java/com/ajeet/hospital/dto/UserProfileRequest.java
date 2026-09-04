package com.ajeet.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileRequest {

    @NotBlank(message = "Username is required")
    @Size(
            min = 3,
            max = 50,
            message = "Username must be between 3 and 50 characters"
    )
    private String username;

    private String name;

    @Email(message = "Invalid email")
    private String email;

    private String phone;

    // Patient-specific
    private LocalDate dateOfBirth;

    private String gender;

    private String address;
}