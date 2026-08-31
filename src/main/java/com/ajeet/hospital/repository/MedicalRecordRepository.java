package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord,Long> {

    List<MedicalRecord> findByPatientId(Long patientId);
}
