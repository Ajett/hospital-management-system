package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientResponse {
    private Long id;

    private String name;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    private String email;

    private String address;
}
