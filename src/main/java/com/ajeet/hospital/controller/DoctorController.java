package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.DoctorRequest;
import com.ajeet.hospital.dto.DoctorResponse;
import com.ajeet.hospital.entity.Doctor;
import com.ajeet.hospital.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public DoctorResponse createDoctor(
            @Valid @RequestBody DoctorRequest request) {

        return doctorService.createDoctor(request);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<DoctorResponse> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public DoctorResponse getDoctorById(
            @PathVariable Long id) {

        return doctorService.getDoctorById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public DoctorResponse updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest request) {

        return doctorService.updateDoctor(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable Long id) {

        doctorService.deleteDoctor(id);

        return "Doctor deleted successfully";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/advanced")
    public List<DoctorResponse> searchDoctors(
            @RequestParam String specialization,
            @RequestParam Long departmentId) {

        return doctorService.findBySpecializationAndDepartment(
                specialization,
                departmentId
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/by-name")
    public List<DoctorResponse> searchByNameAndDepartment(
            @RequestParam String name,
            @RequestParam Long departmentId) {

        return doctorService.searchByNameAndDepartment(
                name,
                departmentId
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public Page<DoctorResponse> searchDoctors(
            @RequestParam String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
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

        return doctorService.searchDoctors(
                specialization,
                page,
                size,
                sortBy,
                direction
        );
    }
}
