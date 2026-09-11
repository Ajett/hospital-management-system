package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.PatientRequest;
import com.ajeet.hospital.dto.PatientResponse;
import com.ajeet.hospital.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public PatientResponse createPatient(
            @Valid @RequestBody PatientRequest request) {

        return patientService.createPatient(request);
    }

    // =========================================================
    // ADMIN - GET ALL ACTIVE PATIENTS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PatientResponse> getAllPatients() {

        return patientService.getAllPatients();
    }

    // =========================================================
    // ADMIN - GET PATIENT BY ID
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public PatientResponse getPatientById(
            @PathVariable Long id) {

        return patientService.getPatientById(id);
    }

    // =========================================================
    // ADMIN - UPDATE PATIENT
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
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
    // ADMIN - SOFT DELETE PATIENT
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deletePatient(
            @PathVariable Long id) {

        patientService.deletePatient(id);

        return "Patient deleted successfully";
    }

    // =========================================================
    // ADMIN - RESTORE PATIENT
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public String restorePatient(
            @PathVariable Long id) {

        patientService.restorePatient(id);

        return "Patient restored successfully";
    }

    // =========================================================
    // ADMIN - PAGINATION
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
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
    // ADMIN - SEARCH ACTIVE PATIENTS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public List<PatientResponse> searchPatients(
            @RequestParam String name) {

        return patientService.searchPatients(name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/page/all")
    public Page<PatientResponse> getAllPatientsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        return patientService.getAllPatientsPage(
                page,
                size,
                sortBy,
                direction
        );
    }
}