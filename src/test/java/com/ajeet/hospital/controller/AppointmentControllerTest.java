package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.AppointmentRequest;
import com.ajeet.hospital.dto.AppointmentResponse;
import com.ajeet.hospital.dto.AppointmentStatusRequest;
import com.ajeet.hospital.entity.AppointmentStatus;
import com.ajeet.hospital.exception.*;
import com.ajeet.hospital.service.AppointmentService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentControllerTest {

    private MockMvc mockMvc;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {

        appointmentService =
                mock(AppointmentService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new AppointmentController(
                                appointmentService
                        )
                )
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }


    @Test
    void createAppointment_shouldCreateSuccessfully()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);

        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );

        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );

        response.setReason(
                "Regular checkup"
        );

        response.setStatus(
                AppointmentStatus.SCHEDULED
        );

        response.setPatientId(1L);
        response.setPatientName("Ajeet");

        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        response.setSpecialization(
                "Cardiology"
        );


        when(
                appointmentService.createAppointment(
                        any(AppointmentRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                {
                                    "appointmentDate": "2026-09-03",
                                    "appointmentTime": "10:30:00",
                                    "reason": "Regular checkup",
                                    "patientId": 1,
                                    "doctorId": 2
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.appointmentDate")
                                .value("2026-09-01"))
                .andExpect(
                        jsonPath("$.appointmentTime")
                                .value("10:30:00")
                )
                .andExpect(
                        jsonPath("$.reason")
                                .value("Regular checkup")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SCHEDULED")
                )
                .andExpect(
                        jsonPath("$.patientId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ajeet")
                )
                .andExpect(
                        jsonPath("$.doctorId")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.doctorName")
                                .value("Dr. Sharma")
                )
                .andExpect(
                        jsonPath("$.specialization")
                                .value("Cardiology")
                );
    }

    @Test
    void getAllAppointments_shouldReturnAllAppointments()
            throws Exception {

        AppointmentResponse appointment1 =
                new AppointmentResponse();

        appointment1.setId(1L);
        appointment1.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        appointment1.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        appointment1.setReason("Regular checkup");
        appointment1.setStatus(
                AppointmentStatus.SCHEDULED
        );
        appointment1.setPatientId(1L);
        appointment1.setPatientName("Ajeet");
        appointment1.setDoctorId(2L);
        appointment1.setDoctorName("Dr. Sharma");
        appointment1.setSpecialization("Cardiology");


        AppointmentResponse appointment2 =
                new AppointmentResponse();

        appointment2.setId(2L);
        appointment2.setAppointmentDate(
                LocalDate.of(2026, 9, 2)
        );
        appointment2.setAppointmentTime(
                LocalTime.of(11, 0)
        );
        appointment2.setReason("Follow-up");
        appointment2.setStatus(
                AppointmentStatus.CONFIRMED
        );
        appointment2.setPatientId(2L);
        appointment2.setPatientName("Rahul");
        appointment2.setDoctorId(3L);
        appointment2.setDoctorName("Dr. Verma");
        appointment2.setSpecialization("Neurology");


        when(
                appointmentService.getAllAppointments()
        ).thenReturn(
                List.of(appointment1, appointment2)
        );


        mockMvc.perform(
                        get("/api/appointments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].appointmentDate").value("2026-09-01"))
                .andExpect(
                        jsonPath("$[0].appointmentTime")
                                .value("10:30:00")
                )
                .andExpect(
                        jsonPath("$[0].reason")
                                .value("Regular checkup")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("SCHEDULED")
                )
                .andExpect(
                        jsonPath("$[0].patientName")
                                .value("Ajeet")
                )
                .andExpect(
                        jsonPath("$[0].doctorName")
                                .value("Dr. Sharma")
                )

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(
                        jsonPath("$[1].appointmentDate")
                                .value("2026-09-02")
                )
                .andExpect(
                        jsonPath("$[1].appointmentTime")
                                .value("11:00:00")
                )
                .andExpect(
                        jsonPath("$[1].reason")
                                .value("Follow-up")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("CONFIRMED")
                )
                .andExpect(
                        jsonPath("$[1].patientName")
                                .value("Rahul")
                )
                .andExpect(
                        jsonPath("$[1].doctorName")
                                .value("Dr. Verma")
                );
    }

    @Test
    void getAppointmentById_shouldReturnAppointment()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        response.setReason("Regular checkup");
        response.setStatus(
                AppointmentStatus.SCHEDULED
        );

        response.setPatientId(1L);
        response.setPatientName("Ajeet");

        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.getAppointmentById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/appointments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.appointmentDate").value("2026-09-01"))
                .andExpect(jsonPath("$.appointmentDate").value("2026-09-01"))
                .andExpect(
                        jsonPath("$.reason")
                                .value("Regular checkup")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SCHEDULED")
                )
                .andExpect(
                        jsonPath("$.patientId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ajeet")
                )
                .andExpect(
                        jsonPath("$.doctorId")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.doctorName")
                                .value("Dr. Sharma")
                )
                .andExpect(
                        jsonPath("$.specialization")
                                .value("Cardiology")
                );
    }

    @Test
    void getAppointmentById_shouldReturn404WhenAppointmentNotFound()
            throws Exception {

        when(
                appointmentService.getAppointmentById(999L)
        ).thenThrow(
                new AppointmentNotFoundException(
                        "Appointment with id 999 not found"
                )
        );

        mockMvc.perform(
                        get("/api/appointments/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Appointment with id 999 not found")
                );

        verify(
                appointmentService
        ).getAppointmentById(999L);
    }

    @Test
    void createAppointment_shouldReturn409WhenDoctorAlreadyBooked()
            throws Exception {

        when(
                appointmentService.createAppointment(
                        any(AppointmentRequest.class)
                )
        ).thenThrow(
                new AppointmentConflictException(
                        "Doctor is already booked for this date and time"
                )
        );

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Doctor is already booked for this date and time"
                                )
                );

        verify(
                appointmentService
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn404WhenPatientNotFound()
            throws Exception {

        when(
                appointmentService.createAppointment(
                        any(AppointmentRequest.class)
                )
        ).thenThrow(
                new PatientNotFoundException(
                        "Patient with id 999 not found"
                )
        );

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
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
                appointmentService
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn404WhenDoctorNotFound()
            throws Exception {

        when(
                appointmentService.createAppointment(
                        any(AppointmentRequest.class)
                )
        ).thenThrow(
                new DoctorNotFoundException(
                        "Doctor with id 999 not found"
                )
        );

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
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
                appointmentService
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn400WhenAppointmentDateIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn400WhenAppointmentTimeIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "reason": "Regular checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn400WhenPatientIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn400WhenDoctorIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
                                "patientId": 1
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn400WhenAppointmentDateIsInPast()
            throws Exception {

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2025-01-01",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldReturn400WhenReasonExceeds500Characters()
            throws Exception {

        String reason = "a".repeat(501);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "%s",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """.formatted(reason))
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldAcceptReasonWith500Characters()
            throws Exception {

        String reason = "a".repeat(500);

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        response.setReason(reason);
        response.setStatus(
                AppointmentStatus.SCHEDULED
        );
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.createAppointment(
                        any(AppointmentRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "%s",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """.formatted(reason))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reason").value(reason))
                .andExpect(
                        jsonPath("$.status")
                                .value("SCHEDULED")
                );

        verify(
                appointmentService
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void getAllAppointments_shouldReturnEmptyListWhenNoAppointments()
            throws Exception {

        when(
                appointmentService.getAllAppointments()
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/appointments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(
                appointmentService
        ).getAllAppointments();
    }

    @Test
    void updateAppointment_shouldUpdateSuccessfully()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 5)
        );
        response.setAppointmentTime(
                LocalTime.of(11, 30)
        );
        response.setReason("Updated checkup");
        response.setStatus(
                AppointmentStatus.SCHEDULED
        );
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.updateAppointment(
                        anyLong(),
                        any(AppointmentRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.appointmentDate")
                                .value("2026-09-05")
                )
                .andExpect(
                        jsonPath("$.appointmentTime")
                                .value("11:30:00")
                )
                .andExpect(
                        jsonPath("$.reason")
                                .value("Updated checkup")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SCHEDULED")
                )
                .andExpect(
                        jsonPath("$.patientId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.doctorId")
                                .value(2)
                );

        verify(
                appointmentService
        ).updateAppointment(
                eq(1L),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn404WhenAppointmentNotFound()
            throws Exception {

        when(
                appointmentService.updateAppointment(
                        eq(999L),
                        any(AppointmentRequest.class)
                )
        ).thenThrow(
                new AppointmentNotFoundException(
                        "Appointment with id 999 not found"
                )
        );

        mockMvc.perform(
                        put("/api/appointments/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Appointment with id 999 not found")
                );

        verify(
                appointmentService
        ).updateAppointment(
                eq(999L),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void deleteAppointment_shouldDeleteSuccessfully()
            throws Exception {

        doNothing()
                .when(appointmentService)
                .deleteAppointment(1L);

        mockMvc.perform(
                        delete("/api/appointments/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                "Appointment deleted successfully"
                        )
                );

        verify(
                appointmentService
        ).deleteAppointment(1L);
    }

    @Test
    void deleteAppointment_shouldReturn404WhenAppointmentNotFound()
            throws Exception {

        doThrow(
                new AppointmentNotFoundException(
                        "Appointment with id 999 not found"
                )
        )
                .when(appointmentService)
                .deleteAppointment(999L);

        mockMvc.perform(
                        delete("/api/appointments/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Appointment with id 999 not found")
                );

        verify(
                appointmentService
        ).deleteAppointment(999L);
    }

    @Test
    void updateAppointmentStatus_shouldChangeScheduledToConfirmed()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        response.setReason("Regular checkup");
        response.setStatus(
                AppointmentStatus.CONFIRMED
        );
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.updateAppointmentStatus(
                        1L,
                        AppointmentStatus.CONFIRMED
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CONFIRMED"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.status")
                                .value("CONFIRMED")
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ajeet")
                )
                .andExpect(
                        jsonPath("$.doctorName")
                                .value("Dr. Sharma")
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                1L,
                AppointmentStatus.CONFIRMED
        );
    }

    @Test
    void updateAppointmentStatus_shouldChangeScheduledToCancelled()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        response.setReason("Regular checkup");
        response.setStatus(
                AppointmentStatus.CANCELLED
        );

        response.setPatientId(1L);
        response.setPatientName("Ajeet");

        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.updateAppointmentStatus(
                        1L,
                        AppointmentStatus.CANCELLED
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CANCELLED"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.status")
                                .value("CANCELLED")
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ajeet")
                )
                .andExpect(
                        jsonPath("$.doctorName")
                                .value("Dr. Sharma")
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                1L,
                AppointmentStatus.CANCELLED
        );
    }

    @Test
    void updateAppointmentStatus_shouldChangeConfirmedToCompleted()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        response.setReason("Regular checkup");
        response.setStatus(
                AppointmentStatus.COMPLETED
        );

        response.setPatientId(1L);
        response.setPatientName("Ajeet");

        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.updateAppointmentStatus(
                        1L,
                        AppointmentStatus.COMPLETED
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "COMPLETED"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ajeet")
                )
                .andExpect(
                        jsonPath("$.doctorName")
                                .value("Dr. Sharma")
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                1L,
                AppointmentStatus.COMPLETED
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn404WhenAppointmentNotFound()
            throws Exception {

        when(
                appointmentService.updateAppointmentStatus(
                        999L,
                        AppointmentStatus.CONFIRMED
                )
        ).thenThrow(
                new AppointmentNotFoundException(
                        "Appointment with id 999 not found"
                )
        );

        mockMvc.perform(
                        patch("/api/appointments/999/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CONFIRMED"
                            }
                            """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Appointment with id 999 not found")
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                999L,
                AppointmentStatus.CONFIRMED
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400ForInvalidTransition()
            throws Exception {

        when(
                appointmentService.updateAppointmentStatus(
                        1L,
                        AppointmentStatus.CANCELLED
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid status transition from CONFIRMED to CANCELLED"
                )
        );

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CANCELLED"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid status transition from CONFIRMED to CANCELLED"
                                )
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                1L,
                AppointmentStatus.CANCELLED
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenStatusIsMissing()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointmentStatus(
                anyLong(),
                any(AppointmentStatus.class)
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenStatusIsNull()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": null
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointmentStatus(
                anyLong(),
                any(AppointmentStatus.class)
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenStatusIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "INVALID"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointmentStatus(
                anyLong(),
                any(AppointmentStatus.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/abc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void deleteAppointment_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        delete("/api/appointments/abc")
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).deleteAppointment(anyLong());
    }

    @Test
    void createAppointment_shouldReturn400WhenJsonIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Regular checkup",
                                "patientId": 1,
                                "doctorId": 2
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenAppointmentDateIsMissing()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenAppointmentTimeIsMissing()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenPatientIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenDoctorIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenReasonExceeds500Characters()
            throws Exception {

        String reason = "a".repeat(501);

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "%s",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """.formatted(reason))
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenAppointmentDateIsInPast()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2025-01-01",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void deleteAppointment_shouldReturn404WhenIdIsNegative()
            throws Exception {

        doThrow(
                new AppointmentNotFoundException(
                        "Appointment with id -1 not found"
                )
        )
                .when(appointmentService)
                .deleteAppointment(-1L);

        mockMvc.perform(
                        delete("/api/appointments/-1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Appointment with id -1 not found")
                );

        verify(
                appointmentService
        ).deleteAppointment(-1L);
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments/abc/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CONFIRMED"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointmentStatus(
                anyLong(),
                any(AppointmentStatus.class)
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenRequestBodyIsEmpty()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointmentStatus(
                anyLong(),
                any(AppointmentStatus.class)
        );
    }

    @Test
    void updateAppointment_shouldAcceptNullReason()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 5)
        );
        response.setAppointmentTime(
                LocalTime.of(11, 30)
        );
        response.setReason(null);
        response.setStatus(
                AppointmentStatus.SCHEDULED
        );
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.updateAppointment(
                        eq(1L),
                        any(AppointmentRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": null,
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reason").doesNotExist());

        verify(
                appointmentService
        ).updateAppointment(
                eq(1L),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointment_shouldReturn400WhenJsonIsInvalid()
            throws Exception {

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "reason": "Updated checkup",
                                "patientId": 1,
                                "doctorId": 2
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointment(
                anyLong(),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void createAppointment_shouldAcceptMissingReason()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 1)
        );
        response.setAppointmentTime(
                LocalTime.of(10, 30)
        );
        response.setReason(null);
        response.setStatus(
                AppointmentStatus.SCHEDULED
        );
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.createAppointment(
                        any(AppointmentRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.status")
                                .value("SCHEDULED")
                );

        verify(
                appointmentService
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void getAllAppointments_shouldCallServiceOnce()
            throws Exception {

        when(
                appointmentService.getAllAppointments()
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/appointments")
                )
                .andExpect(status().isOk());

        verify(
                appointmentService,
                times(1)
        ).getAllAppointments();
    }

    @Test
    void deleteAppointment_shouldCallServiceOnce()
            throws Exception {

        doNothing()
                .when(appointmentService)
                .deleteAppointment(1L);

        mockMvc.perform(
                        delete("/api/appointments/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                "Appointment deleted successfully"
                        )
                );

        verify(
                appointmentService,
                times(1)
        ).deleteAppointment(1L);
    }

    @Test
    void createAppointment_shouldReturn405ForGetRequest()
            throws Exception {

        mockMvc.perform(
                        get("/api/appointments")
                )
                .andExpect(status().isOk());
    }

    @Test
    void createAppointment_shouldReturn405ForPatchRequest()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-03",
                                "appointmentTime": "10:30:00",
                                "reason": "Checkup",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isMethodNotAllowed());

        verify(
                appointmentService,
                never()
        ).createAppointment(
                any(AppointmentRequest.class)
        );
    }

    @Test
    void getAppointmentById_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/api/appointments/abc")
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).getAppointmentById(anyLong());
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenRequestBodyIsMissing()
            throws Exception {

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(
                appointmentService,
                never()
        ).updateAppointmentStatus(
                anyLong(),
                any(AppointmentStatus.class)
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenCancelledToConfirmed()
            throws Exception {

        when(
                appointmentService.updateAppointmentStatus(
                        1L,
                        AppointmentStatus.CONFIRMED
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid status transition from CANCELLED to CONFIRMED"
                )
        );

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CONFIRMED"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid status transition from CANCELLED to CONFIRMED"
                                )
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                1L,
                AppointmentStatus.CONFIRMED
        );
    }

    @Test
    void updateAppointment_shouldAcceptMissingReason()
            throws Exception {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(1L);
        response.setAppointmentDate(
                LocalDate.of(2026, 9, 5)
        );
        response.setAppointmentTime(
                LocalTime.of(11, 30)
        );
        response.setReason(null);
        response.setStatus(
                AppointmentStatus.SCHEDULED
        );
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");
        response.setSpecialization("Cardiology");

        when(
                appointmentService.updateAppointment(
                        eq(1L),
                        any(AppointmentRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentDate": "2026-09-05",
                                "appointmentTime": "11:30:00",
                                "patientId": 1,
                                "doctorId": 2
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(
                appointmentService
        ).updateAppointment(
                eq(1L),
                any(AppointmentRequest.class)
        );
    }

    @Test
    void updateAppointmentStatus_shouldReturn400WhenScheduledToCompleted()
            throws Exception {

        when(
                appointmentService.updateAppointmentStatus(
                        1L,
                        AppointmentStatus.COMPLETED
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid status transition from SCHEDULED to COMPLETED"
                )
        );

        mockMvc.perform(
                        patch("/api/appointments/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "COMPLETED"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid status transition from SCHEDULED to COMPLETED"
                                )
                );

        verify(
                appointmentService
        ).updateAppointmentStatus(
                1L,
                AppointmentStatus.COMPLETED
        );
    }

}
