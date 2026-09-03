package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.PatientRequest;
import com.ajeet.hospital.dto.PatientResponse;
import com.ajeet.hospital.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(
            PatientService patientService) {

        this.patientService = patientService;
    }


    // =========================================================
    // PATIENT - MY PROFILE
    // =========================================================

    @GetMapping("/me")
    public PatientResponse getMyProfile(
            Authentication authentication) {

        String username = authentication.getName();

        return patientService.getMyProfile(username);
    }


    // =========================================================
    // PATIENT - UPDATE MY PROFILE
    // =========================================================

    @PutMapping("/me")
    public PatientResponse updateMyProfile(
            @Valid @RequestBody PatientRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        return patientService.updateMyProfile(
                username,
                request
        );
    }


    // =========================================================
    // ADMIN - CREATE PATIENT
    // =========================================================

    @PostMapping
    public PatientResponse createPatient(
            @Valid @RequestBody PatientRequest request) {

        return patientService.createPatient(request);
    }


    // =========================================================
    // GET ALL PATIENTS
    // =========================================================

    @GetMapping
    public List<PatientResponse> getAllPatients() {

        return patientService.getAllPatients();
    }


    // =========================================================
    // GET PATIENT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public PatientResponse getPatientById(
            @PathVariable Long id) {

        return patientService.getPatientById(id);
    }


    // =========================================================
    // ADMIN - UPDATE PATIENT BY ID
    // =========================================================

    @PutMapping("/{id}")
    public PatientResponse updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {

        return patientService.updatePatient(
                id,
                request
        );
    }


    // =========================================================
    // ADMIN - DELETE PATIENT
    // =========================================================

    @DeleteMapping("/{id}")
    public String deletePatient(
            @PathVariable Long id) {

        patientService.deletePatient(id);

        return "Patient deleted successfully";
    }


    // =========================================================
    // PAGINATION
    // =========================================================

    @GetMapping("/page")
    public Page<PatientResponse> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return patientService.getPatients(
                page,
                size,
                sortBy,
                direction
        );
    }


    // =========================================================
    // SEARCH PATIENTS
    // =========================================================

    @GetMapping("/search")
    public List<PatientResponse> searchPatients(
            @RequestParam String name) {

        return patientService.searchPatients(name);
    }
}