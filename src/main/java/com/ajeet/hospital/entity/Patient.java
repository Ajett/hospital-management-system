package com.ajeet.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    private String email;

    private String address;


    // ============================================
    // LINK PATIENT WITH USER
    // ============================================

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnore
    private User user;
}