package com.ajeet.hospital.controller;

import com.ajeet.hospital.entity.Hospital;
import com.ajeet.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // =========================================================
    // GET ALL
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Hospital> getAllHospitals() {

        return hospitalService.getAllHospitals();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Hospital getHospitalById(
            @PathVariable Long id) {

        return hospitalService.getHospitalById(id);
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Hospital createHospital(
            @RequestBody Hospital hospital) {

        return hospitalService.createHospital(hospital);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Hospital updateHospital(
            @PathVariable Long id,
            @RequestBody Hospital hospital) {

        return hospitalService.updateHospital(
                id,
                hospital
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteHospital(@PathVariable Long id) {

        hospitalService.deleteHospital(id);

        return "Hospital deleted successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public String restoreHospital(@PathVariable Long id) {

        hospitalService.restoreHospital(id);

        return "Hospital restored successfully";
    }
}