package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PatientRepository
        extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserUsername(String username);

    List<Patient> findByNameContainingIgnoreCase(String name);
}
