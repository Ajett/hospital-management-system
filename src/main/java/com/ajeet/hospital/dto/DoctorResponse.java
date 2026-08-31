package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorResponse {
    private Long id;
    private String name;
    private String specialization;
    private String phone;

    private Long departmentId;
    private String departmentName;
}
