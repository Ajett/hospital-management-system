package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.MedicalRecordRequest;
import com.ajeet.hospital.dto.MedicalRecordResponse;
import com.ajeet.hospital.exception.DoctorNotFoundException;
import com.ajeet.hospital.exception.GlobalExceptionHandler;
import com.ajeet.hospital.exception.MedicalRecordNotFoundException;
import com.ajeet.hospital.exception.PatientNotFoundException;
import com.ajeet.hospital.service.MedicalRecordService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MedicalRecordControllerTest {

    private MockMvc mockMvc;

    private MedicalRecordService medicalRecordService;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        medicalRecordService =
                mock(MedicalRecordService.class);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new MedicalRecordController(
                                medicalRecordService
                        )
                )
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }


    @Test
    void createMedicalRecord_shouldCreateSuccessfully()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(1L);
        response.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        response.setDiagnosis("Fever");
        response.setSymptoms("High temperature");
        response.setPrescription("Paracetamol");
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                medicalRecordService.createMedicalRecord(
                        any(MedicalRecordRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "recordDate": "2026-08-30",
                                    "diagnosis": "Fever",
                                    "symptoms": "High temperature",
                                    "prescription": "Paracetamol",
                                    "patientId": 1,
                                    "doctorId": 2
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.recordDate")
                        .value("2026-08-30"))
                .andExpect(jsonPath("$.diagnosis")
                        .value("Fever"))
                .andExpect(jsonPath("$.symptoms")
                        .value("High temperature"))
                .andExpect(jsonPath("$.prescription")
                        .value("Paracetamol"))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$.doctorId").value(2))
                .andExpect(jsonPath("$.doctorName")
                        .value("Dr. Sharma"))
                .andExpect(jsonPath("$.specialization")
                        .value("Cardiology"));
    }

    @Test
    void getAllMedicalRecords_shouldReturnAllRecords()
            throws Exception {

        MedicalRecordResponse record1 =
                new MedicalRecordResponse();

        record1.setId(1L);
        record1.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        record1.setDiagnosis("Fever");
        record1.setSymptoms("High temperature");
        record1.setPrescription("Paracetamol");
        record1.setPatientId(1L);
        record1.setPatientName("Ajeet");
        record1.setDoctorId(2L);
        record1.setDoctorName("Dr. Sharma");
        record1.setSpecialization("Cardiology");


        MedicalRecordResponse record2 =
                new MedicalRecordResponse();

        record2.setId(2L);
        record2.setRecordDate(
                java.time.LocalDate.of(2026, 8, 29)
        );
        record2.setDiagnosis("Cold");
        record2.setSymptoms("Cough");
        record2.setPrescription("Syrup");
        record2.setPatientId(2L);
        record2.setPatientName("Rahul");
        record2.setDoctorId(3L);
        record2.setDoctorName("Dr. Verma");
        record2.setSpecialization("Neurology");


        when(
                medicalRecordService.getAllMedicalRecords()
        ).thenReturn(
                java.util.List.of(record1, record2)
        );


        mockMvc.perform(
                        get("/api/medical-records")
                )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diagnosis")
                        .value("Fever"))
                .andExpect(jsonPath("$[0].patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$[0].doctorName")
                        .value("Dr. Sharma"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].diagnosis")
                        .value("Cold"))
                .andExpect(jsonPath("$[1].patientName")
                        .value("Rahul"))
                .andExpect(jsonPath("$[1].doctorName")
                        .value("Dr. Verma"));
    }

    @Test
    void getMedicalRecordById_shouldReturnRecord()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(1L);
        response.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        response.setDiagnosis("Fever");
        response.setSymptoms("High temperature");
        response.setPrescription("Paracetamol");

        response.setPatientId(1L);
        response.setPatientName("Ajeet");

        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                medicalRecordService.getMedicalRecordById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/medical-records/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.recordDate")
                        .value("2026-08-30"))
                .andExpect(jsonPath("$.diagnosis")
                        .value("Fever"))
                .andExpect(jsonPath("$.symptoms")
                        .value("High temperature"))
                .andExpect(jsonPath("$.prescription")
                        .value("Paracetamol"))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$.doctorId").value(2))
                .andExpect(jsonPath("$.doctorName")
                        .value("Dr. Sharma"))
                .andExpect(jsonPath("$.specialization")
                        .value("Cardiology"));
    }

    @Test
    void getMedicalRecordById_shouldReturn404WhenRecordNotFound()
            throws Exception {

        when(
                medicalRecordService.getMedicalRecordById(999L)
        ).thenThrow(
                new MedicalRecordNotFoundException(
                        "Medical record with id 999 not found"
                )
        );

        mockMvc.perform(
                        get("/api/medical-records/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Medical record with id 999 not found")
                );
    }

    @Test
    void getPatientMedicalHistory_shouldReturnRecords()
            throws Exception {

        MedicalRecordResponse record1 =
                new MedicalRecordResponse();

        record1.setId(1L);
        record1.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        record1.setDiagnosis("Fever");
        record1.setSymptoms("High temperature");
        record1.setPrescription("Paracetamol");
        record1.setPatientId(1L);
        record1.setPatientName("Ajeet");
        record1.setDoctorId(2L);
        record1.setDoctorName("Dr. Sharma");
        record1.setSpecialization("Cardiology");


        MedicalRecordResponse record2 =
                new MedicalRecordResponse();

        record2.setId(2L);
        record2.setRecordDate(
                java.time.LocalDate.of(2026, 8, 20)
        );
        record2.setDiagnosis("Cold");
        record2.setSymptoms("Cough");
        record2.setPrescription("Syrup");
        record2.setPatientId(1L);
        record2.setPatientName("Ajeet");
        record2.setDoctorId(3L);
        record2.setDoctorName("Dr. Verma");
        record2.setSpecialization("Neurology");


        when(
                medicalRecordService
                        .getPatientMedicalHistory(1L)
        ).thenReturn(
                java.util.List.of(record1, record2)
        );


        mockMvc.perform(
                        get("/api/medical-records/patient/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diagnosis")
                        .value("Fever"))
                .andExpect(jsonPath("$[0].patientId")
                        .value(1))
                .andExpect(jsonPath("$[0].patientName")
                        .value("Ajeet"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].diagnosis")
                        .value("Cold"))
                .andExpect(jsonPath("$[1].patientId")
                        .value(1))
                .andExpect(jsonPath("$[1].patientName")
                        .value("Ajeet"));
    }

    @Test
    void getPatientMedicalHistory_shouldReturn404WhenPatientNotFound()
            throws Exception {

        when(
                medicalRecordService
                        .getPatientMedicalHistory(999L)
        ).thenThrow(
                new PatientNotFoundException(
                        "Patient with id 999 not found"
                )
        );

        mockMvc.perform(
                        get("/api/medical-records/patient/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Patient with id 999 not found")
                );

        verify(
                medicalRecordService
        ).getPatientMedicalHistory(999L);
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenRecordDateIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenDiagnosisIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenSymptomsIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }
    @Test
    void createMedicalRecord_shouldReturn400WhenPrescriptionIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenPatientIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenDoctorIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenDiagnosisIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "   ",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenSymptomsIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "   ",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenPrescriptionIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "   ",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenPatientIdIsNull()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": null,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenDoctorIdIsNull()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": null
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn404WhenPatientNotFound()
            throws Exception {

        when(
                medicalRecordService.createMedicalRecord(
                        any(MedicalRecordRequest.class)
                )
        ).thenThrow(
                new PatientNotFoundException(
                        "Patient with id 999 not found"
                )
        );

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 999,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Patient with id 999 not found")
                );

        verify(
                medicalRecordService
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn404WhenDoctorNotFound()
            throws Exception {

        when(
                medicalRecordService.createMedicalRecord(
                        any(MedicalRecordRequest.class)
                )
        ).thenThrow(
                new DoctorNotFoundException(
                        "Doctor with id 999 not found"
                )
        );

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 999
                            }
                            """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Doctor with id 999 not found")
                );

        verify(
                medicalRecordService
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void getPatientMedicalHistory_shouldReturnEmptyListWhenNoRecords()
            throws Exception {

        when(
                medicalRecordService.getPatientMedicalHistory(1L)
        ).thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/medical-records/patient/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(
                medicalRecordService
        ).getPatientMedicalHistory(1L);
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenRecordDateIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "30-08-2026",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenJsonIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void getAllMedicalRecords_shouldReturnEmptyListWhenNoRecords()
            throws Exception {

        when(
                medicalRecordService.getAllMedicalRecords()
        ).thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/medical-records")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(
                medicalRecordService
        ).getAllMedicalRecords();
    }

    @Test
    void getMedicalRecordById_shouldPassCorrectId()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(0L);
        response.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        response.setDiagnosis("Fever");
        response.setSymptoms("Cough");
        response.setPrescription("Medicine");
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                medicalRecordService.getMedicalRecordById(0L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/medical-records/0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.diagnosis")
                        .value("Fever"));

        verify(
                medicalRecordService
        ).getMedicalRecordById(0L);
    }

    @Test
    void getPatientMedicalHistory_shouldPassCorrectPatientId()
            throws Exception {

        when(
                medicalRecordService.getPatientMedicalHistory(0L)
        ).thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/medical-records/patient/0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(
                medicalRecordService
        ).getPatientMedicalHistory(0L);
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenRequestBodyIsEmpty()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldPassPatientIdCorrectly()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(1L);
        response.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        response.setDiagnosis("Fever");
        response.setSymptoms("Cough");
        response.setPrescription("Medicine");
        response.setPatientId(0L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                medicalRecordService.createMedicalRecord(
                        any(MedicalRecordRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "Cough",
                                "prescription": "Medicine",
                                "patientId": 0,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(0));

        verify(
                medicalRecordService
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldPassDoctorIdCorrectly()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(1L);
        response.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        response.setDiagnosis("Fever");
        response.setSymptoms("Cough");
        response.setPrescription("Medicine");
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(0L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                medicalRecordService.createMedicalRecord(
                        any(MedicalRecordRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "Cough",
                                "prescription": "Medicine",
                                "patientId": 1,
                                "doctorId": 0
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(0));

        verify(
                medicalRecordService
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenDiagnosisIsEmpty()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenSymptomsIsEmpty()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenPrescriptionIsEmpty()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn415WhenContentTypeIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isUnsupportedMediaType());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn400WhenRequestBodyIsNull()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldReturn405ForPutRequest()
            throws Exception {

        mockMvc.perform(
                        put("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isMethodNotAllowed());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void getAllMedicalRecords_shouldReturn405ForDeleteRequest()
            throws Exception {

        mockMvc.perform(
                        delete("/api/medical-records")
                )
                .andExpect(status().isMethodNotAllowed());

        verify(
                medicalRecordService,
                never()
        ).getAllMedicalRecords();
    }

    @Test
    void getMedicalRecordById_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/api/medical-records/abc")
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).getMedicalRecordById(anyLong());
    }

    @Test
    void getPatientMedicalHistory_shouldReturn400WhenPatientIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/api/medical-records/patient/abc")
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).getPatientMedicalHistory(anyLong());
    }

    @Test
    void getAllMedicalRecords_shouldCallServiceOnce()
            throws Exception {

        when(
                medicalRecordService.getAllMedicalRecords()
        ).thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/medical-records")
                )
                .andExpect(status().isOk());

        verify(
                medicalRecordService,
                times(1)
        ).getAllMedicalRecords();
    }

    @Test
    void getMedicalRecordById_shouldCallServiceOnce()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(1L);
        response.setDiagnosis("Fever");

        when(
                medicalRecordService.getMedicalRecordById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/medical-records/1")
                )
                .andExpect(status().isOk());

        verify(
                medicalRecordService,
                times(1)
        ).getMedicalRecordById(1L);
    }

    @Test
    void createMedicalRecord_shouldCallServiceOnce()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(1L);
        response.setDiagnosis("Fever");

        when(
                medicalRecordService.createMedicalRecord(
                        any(MedicalRecordRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isOk());

        verify(
                medicalRecordService,
                times(1)
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldNotCallServiceWhenPatientIdMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldNotCallServiceWhenDoctorIdMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldNotCallServiceWhenDiagnosisMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "symptoms": "High temperature",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldNotCallServiceWhenSymptomsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "prescription": "Paracetamol",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void createMedicalRecord_shouldNotCallServiceWhenPrescriptionMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "recordDate": "2026-08-30",
                                "diagnosis": "Fever",
                                "symptoms": "High temperature",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void getMedicalRecordById_shouldReturn404WhenIdIsNegative()
            throws Exception {

        when(
                medicalRecordService.getMedicalRecordById(-1L)
        ).thenThrow(
                new MedicalRecordNotFoundException(
                        "Medical record with id -1 not found"
                )
        );

        mockMvc.perform(
                        get("/api/medical-records/-1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Medical record with id -1 not found")
                );

        verify(
                medicalRecordService
        ).getMedicalRecordById(-1L);
    }

    @Test
    void getPatientMedicalHistory_shouldReturn404WhenPatientIdIsNegative()
            throws Exception {

        when(
                medicalRecordService.getPatientMedicalHistory(-1L)
        ).thenThrow(
                new PatientNotFoundException(
                        "Patient with id -1 not found"
                )
        );

        mockMvc.perform(
                        get("/api/medical-records/patient/-1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Patient with id -1 not found")
                );

        verify(
                medicalRecordService
        ).getPatientMedicalHistory(-1L);
    }

    @Test
    void getAllMedicalRecords_shouldHandleNullResponse()
            throws Exception {

        when(
                medicalRecordService.getAllMedicalRecords()
        ).thenReturn(null);

        mockMvc.perform(
                        get("/api/medical-records")
                )
                .andExpect(status().isOk());

        verify(
                medicalRecordService,
                times(1)
        ).getAllMedicalRecords();
    }

    @Test
    void getMedicalRecordById_shouldReturnNullResponse()
            throws Exception {

        when(
                medicalRecordService.getMedicalRecordById(1L)
        ).thenReturn(null);

        mockMvc.perform(
                        get("/api/medical-records/1")
                )
                .andExpect(status().isOk());

        verify(
                medicalRecordService,
                times(1)
        ).getMedicalRecordById(1L);
    }

    @Test
    void getPatientMedicalHistory_shouldReturnCompleteRecordDetails()
            throws Exception {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        response.setId(10L);
        response.setRecordDate(
                java.time.LocalDate.of(2026, 8, 30)
        );
        response.setDiagnosis("Diabetes");
        response.setSymptoms("High sugar level");
        response.setPrescription("Medicine A");

        response.setPatientId(5L);
        response.setPatientName("Ajeet");

        response.setDoctorId(7L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("General Medicine");

        when(
                medicalRecordService.getPatientMedicalHistory(5L)
        ).thenReturn(java.util.List.of(response));

        mockMvc.perform(
                        get("/api/medical-records/patient/5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].recordDate")
                        .value("2026-08-30"))
                .andExpect(jsonPath("$[0].diagnosis")
                        .value("Diabetes"))
                .andExpect(jsonPath("$[0].symptoms")
                        .value("High sugar level"))
                .andExpect(jsonPath("$[0].prescription")
                        .value("Medicine A"))
                .andExpect(jsonPath("$[0].patientId").value(5))
                .andExpect(jsonPath("$[0].patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$[0].doctorId").value(7))
                .andExpect(jsonPath("$[0].doctorName")
                        .value("Dr. Sharma"))
                .andExpect(jsonPath("$[0].specialization")
                        .value("General Medicine"));

        verify(
                medicalRecordService
        ).getPatientMedicalHistory(5L);
    }

    @Test
    void createMedicalRecord_shouldNotCallServiceWhenRequestBodyIsEmpty()
            throws Exception {

        mockMvc.perform(
                        post("/api/medical-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(
                medicalRecordService,
                never()
        ).createMedicalRecord(
                any(MedicalRecordRequest.class)
        );
    }

    @Test
    void getPatientMedicalHistory_shouldReturnMultipleRecords()
            throws Exception {

        MedicalRecordResponse record1 =
                new MedicalRecordResponse();

        record1.setId(1L);
        record1.setRecordDate(
                java.time.LocalDate.of(2026, 8, 1)
        );
        record1.setDiagnosis("Fever");
        record1.setSymptoms("High temperature");
        record1.setPrescription("Paracetamol");
        record1.setPatientId(1L);
        record1.setPatientName("Ajeet");
        record1.setDoctorId(2L);
        record1.setDoctorName("Dr. Sharma");
        record1.setSpecialization("General Medicine");


        MedicalRecordResponse record2 =
                new MedicalRecordResponse();

        record2.setId(2L);
        record2.setRecordDate(
                java.time.LocalDate.of(2026, 8, 20)
        );
        record2.setDiagnosis("Cold");
        record2.setSymptoms("Cough");
        record2.setPrescription("Syrup");
        record2.setPatientId(1L);
        record2.setPatientName("Ajeet");
        record2.setDoctorId(3L);
        record2.setDoctorName("Dr. Verma");
        record2.setSpecialization("ENT");


        when(
                medicalRecordService.getPatientMedicalHistory(1L)
        ).thenReturn(
                java.util.List.of(record1, record2)
        );


        mockMvc.perform(
                        get("/api/medical-records/patient/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].doctorName")
                        .value("Dr. Sharma"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].patientId").value(1))
                .andExpect(jsonPath("$[1].doctorId").value(3))
                .andExpect(jsonPath("$[1].doctorName")
                        .value("Dr. Verma"));

        verify(
                medicalRecordService
        ).getPatientMedicalHistory(1L);
    }
}
