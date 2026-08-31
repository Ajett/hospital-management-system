package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DepartmentResponse {
    private Long id;
    private String name;
    private String location;

    private List<DoctorSummary> doctors;

    @Getter
    @Setter
    public static class DoctorSummary {

        private Long id;
        private String name;
        private String specialization;
    }
}
