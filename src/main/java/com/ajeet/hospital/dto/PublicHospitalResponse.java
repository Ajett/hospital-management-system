package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicHospitalResponse {

    private Long id;
    private String name;
    private String location;
    private String address;
    private String phone;
}