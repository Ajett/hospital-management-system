package com.ajeet.hospital.dto;

import com.ajeet.hospital.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusRequest {
    @NotNull(message = "Payment status is required")
    private PaymentStatus status;
}
