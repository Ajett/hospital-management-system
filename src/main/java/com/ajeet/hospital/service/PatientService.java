package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.PatientRequest;
import com.ajeet.hospital.dto.PatientResponse;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.exception.PatientNotFoundException;
import com.ajeet.hospital.repository.PatientRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(
            PatientRepository patientRepository) {

        this.patientRepository = patientRepository;
    }


    // =========================================================
    // PATIENT - GET MY PROFILE
    // =========================================================

    public PatientResponse getMyProfile(
            String username) {

        Patient patient =
                patientRepository
                        .findByUserUsername(username)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient profile not found for user "
                                                + username
                                )
                        );

        return convertToResponse(patient);
    }


    // =========================================================
    // PATIENT - UPDATE MY PROFILE
    // =========================================================

    public PatientResponse updateMyProfile(
            String username,
            PatientRequest request) {

        Patient patient =
                patientRepository
                        .findByUserUsername(username)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient profile not found for user "
                                                + username
                                )
                        );

        patient.setName(request.getName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());

        Patient updatedPatient =
                patientRepository.save(patient);

        return convertToResponse(updatedPatient);
    }


    // =========================================================
    // ADMIN - CREATE PATIENT
    // =========================================================

    public PatientResponse createPatient(
            PatientRequest request) {

        Patient patient = new Patient();

        patient.setName(request.getName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());

        Patient savedPatient =
                patientRepository.save(patient);

        return convertToResponse(savedPatient);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    public PatientResponse getPatientById(
            Long id) {

        Patient patient =
                patientRepository.findById(id)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient with id "
                                                + id
                                                + " not found"
                                )
                        );

        return convertToResponse(patient);
    }


    // =========================================================
    // ADMIN - UPDATE
    // =========================================================

    public PatientResponse updatePatient(
            Long id,
            PatientRequest request) {

        Patient patient =
                patientRepository.findById(id)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient with id "
                                                + id
                                                + " not found"
                                )
                        );

        patient.setName(request.getName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());

        Patient updatedPatient =
                patientRepository.save(patient);

        return convertToResponse(updatedPatient);
    }


    // =========================================================
    // ADMIN - DELETE
    // =========================================================

    public void deletePatient(Long id) {

        if (!patientRepository.existsById(id)) {

            throw new PatientNotFoundException(
                    "Patient with id "
                            + id
                            + " not found"
            );
        }

        patientRepository.deleteById(id);
    }


    // =========================================================
    // PAGINATION
    // =========================================================

    public Page<PatientResponse> getPatients(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        Page<Patient> patients =
                patientRepository.findAll(pageRequest);

        return patients.map(
                this::convertToResponse
        );
    }


    // =========================================================
    // SEARCH
    // =========================================================

    public List<PatientResponse> searchPatients(
            String name) {

        return patientRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // ENTITY → DTO
    // =========================================================

    private PatientResponse convertToResponse(
            Patient patient) {

        PatientResponse response =
                new PatientResponse();

        response.setId(patient.getId());

        response.setName(
                patient.getName()
        );

        response.setDateOfBirth(
                patient.getDateOfBirth()
        );

        response.setGender(
                patient.getGender()
        );

        response.setPhone(
                patient.getPhone()
        );

        response.setEmail(
                patient.getEmail()
        );

        response.setAddress(
                patient.getAddress()
        );

        return response;
    }
}