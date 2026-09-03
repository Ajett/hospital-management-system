package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment,Long> {

    List<Appointment> findByPatientUserUsername(String username);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );
}
