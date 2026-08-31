package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository
        extends JpaRepository<Doctor,Long> {

    @Query("""
        SELECT d
        FROM Doctor d
        WHERE LOWER(d.specialization) = LOWER(:specialization)
        """)
    List<Doctor> findBySpecialization(
            @Param("specialization") String specialization);

    @Query("""
        SELECT d
        FROM Doctor d
        WHERE LOWER(d.specialization) = LOWER(:specialization)
        AND d.department.id = :departmentId
        """)
    List<Doctor> findBySpecializationAndDepartment(
            @Param("specialization") String specialization,
            @Param("departmentId") Long departmentId);

    @Query("""
        SELECT d
        FROM Doctor d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
        AND d.department.id = :departmentId
        """)
    List<Doctor> searchByNameAndDepartment(
            @Param("name") String name,
            @Param("departmentId") Long departmentId
    );

    @Query("""
        SELECT d
        FROM Doctor d
        WHERE LOWER(d.specialization)
              = LOWER(:specialization)
        """)
    Page<Doctor> searchDoctors(
            @Param("specialization") String specialization,
            Pageable pageable
    );
}
