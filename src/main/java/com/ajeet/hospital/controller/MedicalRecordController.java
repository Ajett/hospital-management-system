package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.MedicalRecordRequest;
import com.ajeet.hospital.dto.MedicalRecordResponse;
import com.ajeet.hospital.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // CREATE
    @PostMapping
    public MedicalRecordResponse createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequest request) {

        return medicalRecordService.createMedicalRecord(request);
    }

    // GET ALL
    @GetMapping
    public List<MedicalRecordResponse> getAllMedicalRecords() {

        return medicalRecordService.getAllMedicalRecords();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public MedicalRecordResponse getMedicalRecordById(
            @PathVariable Long id) {

        return medicalRecordService.getMedicalRecordById(id);
    }


    //GET Patient All Records
    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordResponse> getPatientMedicalHistory(
            @PathVariable Long patientId) {

        return medicalRecordService
                .getPatientMedicalHistory(patientId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public MedicalRecordResponse updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordRequest request) {

        return medicalRecordService.updateMedicalRecord(id, request);
    }


    // DELETE
    @DeleteMapping("/{id}")
    public String deleteMedicalRecord(
            @PathVariable Long id) {

        medicalRecordService.deleteMedicalRecord(id);

        return "Medical record deleted successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<MedicalRecordResponse> getAllMedicalRecordsForAdmin() {
        return medicalRecordService.getAllMedicalRecordsForAdmin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public String restoreMedicalRecord(@PathVariable Long id) {

        medicalRecordService.restoreMedicalRecord(id);

        return "Medical record restored successfully";
    }

}
