package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.BillRequest;
import com.ajeet.hospital.dto.BillResponse;
import com.ajeet.hospital.entity.Appointment;
import com.ajeet.hospital.entity.Bill;
import com.ajeet.hospital.entity.PaymentStatus;
import com.ajeet.hospital.exception.AppointmentNotFoundException;
import com.ajeet.hospital.exception.BillAlreadyExistsException;
import com.ajeet.hospital.exception.BillNotFoundException;
import com.ajeet.hospital.repository.AppointmentRepository;
import com.ajeet.hospital.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;

    public BillService(
            BillRepository billRepository,
            AppointmentRepository appointmentRepository) {

        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // CREATE BILL
    public BillResponse createBill(BillRequest request) {

        // Check appointment exists
        Appointment appointment = appointmentRepository
                .findById(request.getAppointmentId())
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment with id "
                                        + request.getAppointmentId()
                                        + " not found"
                        )
                );

        // Check bill already exists
        if (billRepository.existsByAppointmentId(
                request.getAppointmentId())) {

            throw new BillAlreadyExistsException(
                    "Bill already exists for this appointment"
            );
        }

        // Create Bill
        Bill bill = new Bill();

        bill.setAmount(request.getAmount());

        bill.setBillDate(LocalDate.now());

        bill.setPaymentStatus(
                PaymentStatus.PENDING
        );

        bill.setAppointment(appointment);

        // Save
        Bill savedBill =
                billRepository.save(bill);

        return convertToResponse(savedBill);
    }

    // GET ALL BILLS
    public List<BillResponse> getAllBills() {

        return billRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET BILL BY ID
    public BillResponse getBillById(Long id) {

        Bill bill = billRepository
                .findById(id)
                .orElseThrow(() ->
                        new BillNotFoundException(
                                "Bill with id "
                                        + id
                                        + " not found"
                        )
                );

        return convertToResponse(bill);
    }

    @Transactional
    public BillResponse updatePaymentStatus(
            Long id,
            PaymentStatus newStatus) {

        Bill bill = billRepository
                .findById(id)
                .orElseThrow(() ->
                        new BillNotFoundException(
                                "Bill with id " + id + " not found"
                        )
                );

        PaymentStatus currentStatus =
                bill.getPaymentStatus();

        if (currentStatus == PaymentStatus.PENDING
                && newStatus == PaymentStatus.PAID) {

            bill.setPaymentStatus(PaymentStatus.PAID);

        } else if (currentStatus == PaymentStatus.PENDING
                && newStatus == PaymentStatus.CANCELLED) {

            bill.setPaymentStatus(PaymentStatus.CANCELLED);

        } else {

            throw new IllegalStateException(
                    "Invalid payment status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        Bill updatedBill =
                billRepository.save(bill);

        return convertToResponse(updatedBill);
    }

    // ENTITY → RESPONSE DTO
    private BillResponse convertToResponse(Bill bill) {

        BillResponse response =
                new BillResponse();

        response.setId(bill.getId());

        response.setAmount(
                bill.getAmount()
        );

        response.setBillDate(
                bill.getBillDate()
        );

        response.setPaymentStatus(
                bill.getPaymentStatus()
        );

        Appointment appointment =
                bill.getAppointment();

        response.setAppointmentId(
                appointment.getId()
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

        return response;
    }

    // UPDATE BILL
    public BillResponse updateBill(
            Long id,
            BillRequest request) {

        Bill bill = billRepository
                .findById(id)
                .orElseThrow(() ->
                        new BillNotFoundException(
                                "Bill with id "
                                        + id
                                        + " not found"
                        )
                );

        Appointment appointment =
                appointmentRepository
                        .findById(request.getAppointmentId())
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment with id "
                                                + request.getAppointmentId()
                                                + " not found"
                                )
                        );

        // Don't allow another bill for the same appointment
        if (!bill.getAppointment()
                .getId()
                .equals(request.getAppointmentId())
                && billRepository.existsByAppointmentId(
                request.getAppointmentId())) {

            throw new BillAlreadyExistsException(
                    "Bill already exists for this appointment"
            );
        }

        bill.setAmount(
                request.getAmount()
        );

        bill.setAppointment(
                appointment
        );

        Bill updatedBill =
                billRepository.save(bill);

        return convertToResponse(updatedBill);
    }


    // DELETE BILL
    @Transactional
    public void deleteBill(Long id) {

        Bill bill = billRepository
                .findById(id)
                .orElseThrow(() ->
                        new BillNotFoundException(
                                "Bill with id "
                                        + id
                                        + " not found"
                        )
                );

        billRepository.delete(bill);
    }

}
