package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicDoctorResponse {

    private Long id;

    private String name;

    private String specialization;

    private String departmentName;

    private String location;

    private Long hospitalId;

    private String hospitalName;
}