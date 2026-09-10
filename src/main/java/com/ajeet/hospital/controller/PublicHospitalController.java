package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.PublicHospitalResponse;
import com.ajeet.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/hospitals")
@RequiredArgsConstructor
public class PublicHospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public List<PublicHospitalResponse> getHospitals() {
        return hospitalService.getPublicHospitals();
    }

    @GetMapping("/{id}")
    public PublicHospitalResponse getHospital(
            @PathVariable Long id
    ) {
        return hospitalService.getPublicHospital(id);
    }

    @GetMapping("/search")
    public List<PublicHospitalResponse> searchHospitals(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location
    ) {
        return hospitalService.searchPublicHospitals(
                query,
                location
        );
    }
}