package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.BillRequest;
import com.ajeet.hospital.dto.BillResponse;
import com.ajeet.hospital.dto.PaymentStatusRequest;
import com.ajeet.hospital.entity.PaymentStatus;
import com.ajeet.hospital.exception.AppointmentNotFoundException;
import com.ajeet.hospital.exception.BillAlreadyExistsException;
import com.ajeet.hospital.exception.BillNotFoundException;
import com.ajeet.hospital.exception.GlobalExceptionHandler;
import com.ajeet.hospital.service.BillService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillControllerTest {

    private MockMvc mockMvc;

    private BillService billService;


    @BeforeEach
    void setUp() {

        billService =
                mock(BillService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new BillController(billService)
                )
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }


    // =========================================================
    // CREATE BILL
    // =========================================================

    @Test
    void createBill_shouldCreateSuccessfully()
            throws Exception {

        BillResponse response =
                new BillResponse();

        response.setId(1L);
        response.setAmount(
                new BigDecimal("1500.00")
        );
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(
                PaymentStatus.PENDING
        );
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");


        when(
                billService.createBill(
                        any(BillRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        post("/api/bills")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                {
                                    "amount": 1500.00,
                                    "appointmentId": 10
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount")
                        .value(1500.00))
                .andExpect(jsonPath("$.billDate")
                        .value("2026-08-31"))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("PENDING"))
                .andExpect(jsonPath("$.appointmentId")
                        .value(10))
                .andExpect(jsonPath("$.patientId")
                        .value(1))
                .andExpect(jsonPath("$.patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$.doctorId")
                        .value(2))
                .andExpect(jsonPath("$.doctorName")
                        .value("Dr. Sharma"));
    }

    @Test
    void getAllBills_shouldReturnAllBills()
            throws Exception {

        BillResponse bill1 = new BillResponse();

        bill1.setId(1L);
        bill1.setAmount(new BigDecimal("1500.00"));
        bill1.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        bill1.setPaymentStatus(
                PaymentStatus.PENDING
        );
        bill1.setAppointmentId(10L);
        bill1.setPatientId(1L);
        bill1.setPatientName("Ajeet");
        bill1.setDoctorId(2L);
        bill1.setDoctorName("Dr. Sharma");


        BillResponse bill2 = new BillResponse();

        bill2.setId(2L);
        bill2.setAmount(new BigDecimal("2500.00"));
        bill2.setBillDate(
                LocalDate.of(2026, 8, 30)
        );
        bill2.setPaymentStatus(
                PaymentStatus.PAID
        );
        bill2.setAppointmentId(11L);
        bill2.setPatientId(2L);
        bill2.setPatientName("Rahul");
        bill2.setDoctorId(3L);
        bill2.setDoctorName("Dr. Verma");


        when(
                billService.getAllBills()
        ).thenReturn(
                java.util.List.of(bill1, bill2)
        );


        mockMvc.perform(
                        get("/api/bills")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(1500.00))
                .andExpect(jsonPath("$[0].paymentStatus")
                        .value("PENDING"))
                .andExpect(jsonPath("$[0].patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$[0].doctorName")
                        .value("Dr. Sharma"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].amount").value(2500.00))
                .andExpect(jsonPath("$[1].paymentStatus")
                        .value("PAID"))
                .andExpect(jsonPath("$[1].patientName")
                        .value("Rahul"))
                .andExpect(jsonPath("$[1].doctorName")
                        .value("Dr. Verma"));
    }

    @Test
    void getBillById_shouldReturnBill()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("1500.00"));
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(
                PaymentStatus.PENDING
        );
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.getBillById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/bills/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.billDate")
                        .value("2026-08-31"))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("PENDING"))
                .andExpect(jsonPath("$.appointmentId")
                        .value(10))
                .andExpect(jsonPath("$.patientId")
                        .value(1))
                .andExpect(jsonPath("$.patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$.doctorId")
                        .value(2))
                .andExpect(jsonPath("$.doctorName")
                        .value("Dr. Sharma"));
    }

    @Test
    void getBillById_shouldReturn404WhenBillNotFound()
            throws Exception {

        when(
                billService.getBillById(999L)
        ).thenThrow(
                new BillNotFoundException(
                        "Bill with id 999 not found"
                )
        );

        mockMvc.perform(
                        get("/api/bills/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Bill with id 999 not found")
                );

        verify(
                billService
        ).getBillById(999L);
    }

    @Test
    void getAllBills_shouldReturnEmptyListWhenNoBills()
            throws Exception {

        when(
                billService.getAllBills()
        ).thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/bills")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(
                billService
        ).getAllBills();
    }

    @Test
    void createBill_shouldReturn404WhenAppointmentNotFound()
            throws Exception {

        when(
                billService.createBill(
                        any(BillRequest.class)
                )
        ).thenThrow(
                new AppointmentNotFoundException(
                        "Appointment with id 999 not found"
                )
        );

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 1500.00,
                                "appointmentId": 999
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
                billService
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn409WhenBillAlreadyExists()
            throws Exception {

        when(
                billService.createBill(
                        any(BillRequest.class)
                )
        ).thenThrow(
                new BillAlreadyExistsException(
                        "Bill already exists for this appointment"
                )
        );

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 1500.00,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(
                        jsonPath("$.message")
                                .value("Bill already exists for this appointment")
                );

        verify(
                billService
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAmountIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAmountIsZero()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 0,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAmountIsNegative()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": -100,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAppointmentIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 1500.00
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAppointmentIdIsNull()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 1500.00,
                                "appointmentId": null
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAmountIsNull()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": null,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {}
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldAcceptSmallPositiveAmount()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("0.01"));
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(PaymentStatus.PENDING);
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.createBill(
                        any(BillRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 0.01,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0.01))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("PENDING"));
    }

    @Test
    void updatePaymentStatus_shouldChangePendingToPaid()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("1500.00"));
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(PaymentStatus.PAID);
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.PAID
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("PAID"))
                .andExpect(jsonPath("$.appointmentId")
                        .value(10))
                .andExpect(jsonPath("$.patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$.doctorName")
                        .value("Dr. Sharma"));

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.PAID
        );
    }

    @Test
    void updatePaymentStatus_shouldChangePendingToCancelled()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("1500.00"));
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(
                PaymentStatus.CANCELLED
        );
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.CANCELLED
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CANCELLED"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("CANCELLED"))
                .andExpect(jsonPath("$.appointmentId")
                        .value(10))
                .andExpect(jsonPath("$.patientName")
                        .value("Ajeet"))
                .andExpect(jsonPath("$.doctorName")
                        .value("Dr. Sharma"));

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.CANCELLED
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn404WhenBillNotFound()
            throws Exception {

        when(
                billService.updatePaymentStatus(
                        999L,
                        PaymentStatus.PAID
                )
        ).thenThrow(
                new BillNotFoundException(
                        "Bill with id 999 not found"
                )
        );

        mockMvc.perform(
                        patch("/api/bills/999/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Bill with id 999 not found")
                );

        verify(
                billService
        ).updatePaymentStatus(
                999L,
                PaymentStatus.PAID
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenStatusIsMissing()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenStatusIsNull()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": null
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenStatusIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "INVALID"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400ForInvalidTransition()
            throws Exception {

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.CANCELLED
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid payment status transition from PAID to CANCELLED"
                )
        );

        mockMvc.perform(
                        patch("/api/bills/1/status")
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
                                        "Invalid payment status transition from PAID to CANCELLED"
                                )
                );

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.CANCELLED
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/abc/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldPassCorrectIdAndStatus()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(5L);
        response.setAmount(new BigDecimal("2000.00"));
        response.setPaymentStatus(PaymentStatus.PAID);
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.updatePaymentStatus(
                        5L,
                        PaymentStatus.PAID
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/bills/5/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("PAID"));

        verify(
                billService
        ).updatePaymentStatus(
                5L,
                PaymentStatus.PAID
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenRequestBodyIsEmpty()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn405ForGetRequest()
            throws Exception {

        mockMvc.perform(
                        get("/api/bills/1/status")
                )
                .andExpect(status().isMethodNotAllowed());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenJsonIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 1500.00,
                                "appointmentId": 10
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void getBillById_shouldReturn400WhenIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/api/bills/abc")
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).getBillById(anyLong());
    }

    @Test
    void getAllBills_shouldReturn405ForDeleteRequest()
            throws Exception {

        mockMvc.perform(
                        delete("/api/bills")
                )
                .andExpect(status().isMethodNotAllowed());

        verify(
                billService,
                never()
        ).getAllBills();
    }

    @Test
    void updatePaymentStatus_shouldReturn415WhenContentTypeIsMissing()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isUnsupportedMediaType());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenStatusIsEmpty()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void createBill_shouldReturn400WhenAmountIsNegativeDecimal()
            throws Exception {

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": -0.01,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldAcceptMinimumPositiveAmount()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("0.01"));
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(PaymentStatus.PENDING);
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.createBill(
                        any(BillRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 0.01,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0.01))
                .andExpect(jsonPath("$.paymentStatus")
                        .value("PENDING"));

        verify(
                billService
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void createBill_shouldCallServiceOnce()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("1500.00"));
        response.setPaymentStatus(PaymentStatus.PENDING);
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.createBill(
                        any(BillRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/bills")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 1500.00,
                                "appointmentId": 10
                            }
                            """)
                )
                .andExpect(status().isOk());

        verify(
                billService,
                times(1)
        ).createBill(
                any(BillRequest.class)
        );
    }

    @Test
    void getAllBills_shouldCallServiceOnce()
            throws Exception {

        when(
                billService.getAllBills()
        ).thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/bills")
                )
                .andExpect(status().isOk());

        verify(
                billService,
                times(1)
        ).getAllBills();
    }

    @Test
    void getBillById_shouldCallServiceOnce()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("1500.00"));
        response.setBillDate(
                LocalDate.of(2026, 8, 31)
        );
        response.setPaymentStatus(
                PaymentStatus.PENDING
        );
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.getBillById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/bills/1")
                )
                .andExpect(status().isOk());

        verify(
                billService,
                times(1)
        ).getBillById(1L);
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenPaidToPaid()
            throws Exception {

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.PAID
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid payment status transition from PAID to PAID"
                )
        );

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid payment status transition from PAID to PAID"
                                )
                );

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.PAID
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenCancelledToPaid()
            throws Exception {

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.PAID
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid payment status transition from CANCELLED to PAID"
                )
        );

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "PAID"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid payment status transition from CANCELLED to PAID"
                                )
                );

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.PAID
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenCancelledToCancelled()
            throws Exception {

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.CANCELLED
                )
        ).thenThrow(
                new IllegalStateException(
                        "Invalid payment status transition from CANCELLED to CANCELLED"
                )
        );

        mockMvc.perform(
                        patch("/api/bills/1/status")
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
                                        "Invalid payment status transition from CANCELLED to CANCELLED"
                                )
                );

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.CANCELLED
        );
    }

    @Test
    void updatePaymentStatus_shouldReturn400WhenRequestBodyIsMissing()
            throws Exception {

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(
                billService,
                never()
        ).updatePaymentStatus(
                anyLong(),
                any(PaymentStatus.class)
        );
    }

    @Test
    void updatePaymentStatus_shouldPassCancelledStatusCorrectly()
            throws Exception {

        BillResponse response = new BillResponse();

        response.setId(1L);
        response.setAmount(new BigDecimal("1500.00"));
        response.setPaymentStatus(PaymentStatus.CANCELLED);
        response.setAppointmentId(10L);
        response.setPatientId(1L);
        response.setPatientName("Ajeet");
        response.setDoctorId(2L);
        response.setDoctorName("Dr. Sharma");

        when(
                billService.updatePaymentStatus(
                        1L,
                        PaymentStatus.CANCELLED
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch("/api/bills/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "status": "CANCELLED"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus")
                        .value("CANCELLED"));

        verify(
                billService
        ).updatePaymentStatus(
                1L,
                PaymentStatus.CANCELLED
        );
    }

    @Test
    void getBillById_shouldReturn404WhenIdIsNegative()
            throws Exception {

        when(
                billService.getBillById(-1L)
        ).thenThrow(
                new BillNotFoundException(
                        "Bill with id -1 not found"
                )
        );

        mockMvc.perform(
                        get("/api/bills/-1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Bill with id -1 not found")
                );

        verify(
                billService
        ).getBillById(-1L);
    }
}
