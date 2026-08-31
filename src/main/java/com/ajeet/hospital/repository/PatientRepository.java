package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PatientRepository
        extends JpaRepository<Patient, Long> {

    List<Patient> findByNameContainingIgnoreCase(String name);
}
