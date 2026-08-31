package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class MedicalRecordResponse {
    private Long id;

    private LocalDate recordDate;

    private String diagnosis;

    private String symptoms;

    private String prescription;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;
    private String specialization;
}
