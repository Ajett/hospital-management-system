package com.ajeet.hospital.dto;

import com.ajeet.hospital.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BillResponse {

    private Long id;

    private BigDecimal amount;

    private LocalDate billDate;

    private PaymentStatus paymentStatus;

    private Long appointmentId;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;
}
