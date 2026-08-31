package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillRepository extends JpaRepository<Bill, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    @Modifying
    @Query("DELETE FROM Bill b WHERE b.appointment.id = :appointmentId")
    void deleteByAppointmentId(@Param("appointmentId") Long appointmentId);
}
