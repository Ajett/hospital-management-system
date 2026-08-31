package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.PatientRequest;
import com.ajeet.hospital.dto.PatientResponse;
import com.ajeet.hospital.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // CREATE
    @PostMapping
    public PatientResponse createPatient(
            @Valid @RequestBody PatientRequest request) {

        return patientService.createPatient(request);
    }

    // GET ALL
    @GetMapping
    public List<PatientResponse> getAllPatients() {

        return patientService.getAllPatients();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public PatientResponse getPatientById(
            @PathVariable Long id) {

        return patientService.getPatientById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public PatientResponse updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {

        return patientService.updatePatient(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deletePatient(@PathVariable Long id) {

        patientService.deletePatient(id);

        return "Patient deleted successfully";
    }

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

    @GetMapping("/search")
    public List<PatientResponse> searchPatients(
            @RequestParam String name) {

        return patientService.searchPatients(name);
    }
}
