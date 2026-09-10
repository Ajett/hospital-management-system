package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.PublicDoctorResponse;
import com.ajeet.hospital.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/doctors")
public class PublicDoctorController {

    private final DoctorService doctorService;

    public PublicDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // =========================================================
    // GET ALL PUBLIC DOCTORS
    // =========================================================

    @GetMapping
    public List<PublicDoctorResponse> getPublicDoctors() {
        return doctorService.getPublicDoctors();
    }

    // =========================================================
    // SEARCH PUBLIC DOCTORS
    // =========================================================

    @GetMapping("/search")
    public List<PublicDoctorResponse> searchPublicDoctors(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location) {

        return doctorService.searchPublicDoctors(
                query,
                specialization,
                location
        );
    }

    // =========================================================
    // GET DOCTORS BY HOSPITAL
    // =========================================================

    @GetMapping("/hospital/{hospitalId}")
    public List<PublicDoctorResponse> getDoctorsByHospital(
            @PathVariable Long hospitalId) {

        return doctorService.getPublicDoctorsByHospitalId(
                hospitalId
        );
    }

    // =========================================================
    // GET PUBLIC DOCTOR PROFILE
    // =========================================================

    @GetMapping("/{id}")
    public PublicDoctorResponse getPublicDoctorById(
            @PathVariable Long id) {

        return doctorService.getPublicDoctorById(id);
    }
}