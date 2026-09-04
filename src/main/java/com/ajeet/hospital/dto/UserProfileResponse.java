package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileResponse {

    private Long userId;
    private String username;
    private String role;

    // Generic user profile
    private String name;
    private String email;
    private String phone;

    // Patient-specific profile
    private Long patientId;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
}