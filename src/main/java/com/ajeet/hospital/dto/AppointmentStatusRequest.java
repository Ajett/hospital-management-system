package com.ajeet.hospital.dto;

import com.ajeet.hospital.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentStatusRequest {
    @NotNull(message = "Status is required")
    private AppointmentStatus status;
}

