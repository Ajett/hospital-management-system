package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.AppointmentRequest;
import com.ajeet.hospital.dto.AppointmentResponse;
import com.ajeet.hospital.entity.Appointment;
import com.ajeet.hospital.entity.AppointmentStatus;
import com.ajeet.hospital.entity.Doctor;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.exception.AppointmentConflictException;
import com.ajeet.hospital.exception.AppointmentNotFoundException;
import com.ajeet.hospital.exception.DoctorNotFoundException;
import com.ajeet.hospital.exception.PatientNotFoundException;
import com.ajeet.hospital.repository.AppointmentRepository;
import com.ajeet.hospital.repository.BillRepository;
import com.ajeet.hospital.repository.DoctorRepository;
import com.ajeet.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ajeet.hospital.dto.PatientAppointmentRequest;

import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final BillRepository billRepository;


    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            BillRepository billRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.billRepository = billRepository;
    }



    public AppointmentResponse createAppointment(
            AppointmentRequest request) {

        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                                request.getDoctorId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime()
                        );

        if (alreadyBooked) {
            throw new AppointmentConflictException(
                    "Doctor is already booked for this date and time"
            );
        }

        // Find Patient
        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient with id "
                                        + request.getPatientId()
                                        + " not found"
                        )
                );

        // Find Doctor
        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor with id "
                                        + request.getDoctorId()
                                        + " not found"
                        )
                );

        // Create Appointment
        Appointment appointment = new Appointment();

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setReason(
                request.getReason()
        );

        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return convertToResponse(savedAppointment);
    }

    // =========================================================
// CREATE APPOINTMENT FOR LOGGED-IN PATIENT
// =========================================================

    public AppointmentResponse createPatientAppointment(
            String username,
            PatientAppointmentRequest request) {

        // Find patient using logged-in username
        Patient patient =
                patientRepository
                        .findByUserUsername(username)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient profile not found for user "
                                                + username
                                )
                        );


        // Check doctor availability
        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                                request.getDoctorId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime()
                        );

        if (alreadyBooked) {

            throw new AppointmentConflictException(
                    "Doctor is already booked for this date and time"
            );
        }


        // Find doctor
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


        // Create appointment
        Appointment appointment =
                new Appointment();

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setReason(
                request.getReason()
        );

        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );

        appointment.setPatient(patient);

        appointment.setDoctor(doctor);


        Appointment savedAppointment =
                appointmentRepository.save(appointment);


        return convertToResponse(
                savedAppointment
        );
    }

    public List<AppointmentResponse> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<AppointmentResponse> getAppointmentsForPatient(
            String username) {

        return appointmentRepository
                .findByPatientUserUsername(username)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public AppointmentResponse getAppointmentById(Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment with id " + id + " not found"
                        )
                );

        return convertToResponse(appointment);
    }

    public AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment with id " + id + " not found"
                        )
                );

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setReason(
                request.getReason()
        );

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return convertToResponse(updatedAppointment);
    }




    @Transactional
    public void deleteAppointment(Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment with id " + id + " not found"));


        // First delete Bill
        billRepository.deleteByAppointmentId(id);

        // Then delete Appointment
        appointmentRepository.delete(appointment);
    }



    public AppointmentResponse updateAppointmentStatus(
            Long id,
            AppointmentStatus newStatus) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment with id " + id + " not found"
                        )
                );

        AppointmentStatus currentStatus =
                appointment.getStatus();

        // SCHEDULED → CONFIRMED
        if (currentStatus == AppointmentStatus.SCHEDULED
                && newStatus == AppointmentStatus.CONFIRMED) {

            appointment.setStatus(newStatus);
        }

        // SCHEDULED → CANCELLED
        else if (currentStatus == AppointmentStatus.SCHEDULED
                && newStatus == AppointmentStatus.CANCELLED) {

            appointment.setStatus(newStatus);
        }

        // CONFIRMED → COMPLETED
        else if (currentStatus == AppointmentStatus.CONFIRMED
                && newStatus == AppointmentStatus.COMPLETED) {

            appointment.setStatus(newStatus);
        }

        else {
            throw new IllegalStateException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return convertToResponse(updatedAppointment);
    }


    private AppointmentResponse convertToResponse(
            Appointment appointment) {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(appointment.getId());

        response.setAppointmentDate(
                appointment.getAppointmentDate()
        );

        response.setAppointmentTime(
                appointment.getAppointmentTime()
        );

        response.setReason(
                appointment.getReason()
        );

        response.setStatus(
                appointment.getStatus()
        );

        response.setPatientId(
                appointment.getPatient().getId()
        );

        response.setPatientName(
                appointment.getPatient().getName()
        );

        response.setDoctorId(
                appointment.getDoctor().getId()
        );

        response.setDoctorName(
                appointment.getDoctor().getName()
        );

        response.setSpecialization(
                appointment.getDoctor().getSpecialization()
        );

        return response;
    }


}
