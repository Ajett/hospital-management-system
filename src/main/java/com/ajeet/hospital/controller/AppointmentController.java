package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.AppointmentRequest;
import com.ajeet.hospital.dto.AppointmentResponse;
import com.ajeet.hospital.dto.AppointmentStatusRequest;
import com.ajeet.hospital.dto.PatientAppointmentRequest;
import com.ajeet.hospital.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;


    public AppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService =
                appointmentService;
    }


    // =========================================================
    // ADMIN / GENERAL CREATE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AppointmentResponse createAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        return appointmentService.createAppointment(
                request
        );
    }


    // =========================================================
    // PATIENT CREATE
    // =========================================================

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/patient")
    public AppointmentResponse createPatientAppointment(
            @Valid @RequestBody PatientAppointmentRequest request,
            Authentication authentication) {

        String username =
                authentication.getName();

        return appointmentService.createPatientAppointment(
                username,
                request
        );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AppointmentResponse> getAllAppointments() {

        return appointmentService.getAllAppointments();
    }

    // =========================================================
// GET MY APPOINTMENTS
// =========================================================

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my")
    public List<AppointmentResponse> getMyAppointments(
            Authentication authentication) {

        String username = authentication.getName();

        return appointmentService
                .getAppointmentsForPatient(username);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public AppointmentResponse getAppointmentById(
            @PathVariable Long id) {

        return appointmentService.getAppointmentById(
                id
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public AppointmentResponse updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {

        return appointmentService.updateAppointment(
                id,
                request
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteAppointment(
            @PathVariable Long id) {

        appointmentService.deleteAppointment(id);

        return "Appointment deleted successfully";
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public AppointmentResponse updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusRequest request) {

        return appointmentService.updateAppointmentStatus(
                id,
                request.getStatus()
        );
    }
}