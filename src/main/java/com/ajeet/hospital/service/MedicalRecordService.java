package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.MedicalRecordRequest;
import com.ajeet.hospital.dto.MedicalRecordResponse;
import com.ajeet.hospital.entity.Doctor;
import com.ajeet.hospital.entity.MedicalRecord;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.exception.DoctorNotFoundException;
import com.ajeet.hospital.exception.MedicalRecordNotFoundException;
import com.ajeet.hospital.exception.PatientNotFoundException;
import com.ajeet.hospital.repository.DoctorRepository;
import com.ajeet.hospital.repository.MedicalRecordRepository;
import com.ajeet.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // CREATE
    public MedicalRecordResponse createMedicalRecord(
            MedicalRecordRequest request) {

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient with id "
                                        + request.getPatientId()
                                        + " not found"
                        )
                );

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor with id "
                                        + request.getDoctorId()
                                        + " not found"
                        )
                );

        MedicalRecord record = new MedicalRecord();

        record.setRecordDate(request.getRecordDate());
        record.setDiagnosis(request.getDiagnosis());
        record.setSymptoms(request.getSymptoms());
        record.setPrescription(request.getPrescription());

        record.setPatient(patient);
        record.setDoctor(doctor);

        MedicalRecord savedRecord =
                medicalRecordRepository.save(record);

        return convertToResponse(savedRecord);
    }


    // GET ALL
    public List<MedicalRecordResponse> getAllMedicalRecords() {

        return medicalRecordRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET BY ID
    public MedicalRecordResponse getMedicalRecordById(Long id) {

        MedicalRecord record = medicalRecordRepository
                .findById(id)
                .orElseThrow(() ->
                        new MedicalRecordNotFoundException(
                                "Medical record with id "
                                        + id
                                        + " not found"
                        )
                );

        return convertToResponse(record);
    }

    // ENTITY → RESPONSE DTO
    private MedicalRecordResponse convertToResponse(
            MedicalRecord record) {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(record.getId());
        response.setRecordDate(record.getRecordDate());
        response.setDiagnosis(record.getDiagnosis());
        response.setSymptoms(record.getSymptoms());
        response.setPrescription(record.getPrescription());

        response.setPatientId(
                record.getPatient().getId()
        );

        response.setPatientName(
                record.getPatient().getName()
        );

        response.setDoctorId(
                record.getDoctor().getId()
        );

        response.setDoctorName(
                record.getDoctor().getName()
        );

        response.setSpecialization(
                record.getDoctor().getSpecialization()
        );

        return response;
    }

    public List<MedicalRecordResponse> getPatientMedicalHistory(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException(
                    "Patient with id " + patientId + " not found"
            );
        }

        return medicalRecordRepository
                .findByPatientId(patientId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // UPDATE
    public MedicalRecordResponse updateMedicalRecord(
            Long id,
            MedicalRecordRequest request) {

        MedicalRecord record =
                medicalRecordRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new MedicalRecordNotFoundException(
                                        "Medical record with id "
                                                + id
                                                + " not found"
                                )
                        );

        Patient patient =
                patientRepository
                        .findById(request.getPatientId())
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient with id "
                                                + request.getPatientId()
                                                + " not found"
                                )
                        );

        Doctor doctor =
                doctorRepository
                        .findById(request.getDoctorId())
                        .orElseThrow(() ->
                                new DoctorNotFoundException(
                                        "Doctor with id "
                                                + request.getDoctorId()
                                                + " not found"
                                )
                        );

        record.setRecordDate(
                request.getRecordDate()
        );

        record.setDiagnosis(
                request.getDiagnosis()
        );

        record.setSymptoms(
                request.getSymptoms()
        );

        record.setPrescription(
                request.getPrescription()
        );

        record.setPatient(patient);
        record.setDoctor(doctor);

        MedicalRecord updatedRecord =
                medicalRecordRepository.save(record);

        return convertToResponse(updatedRecord);
    }


    // DELETE
    public void deleteMedicalRecord(Long id) {

        MedicalRecord record =
                medicalRecordRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new MedicalRecordNotFoundException(
                                        "Medical record with id "
                                                + id
                                                + " not found"
                                )
                        );

        medicalRecordRepository.delete(record);
    }

}
