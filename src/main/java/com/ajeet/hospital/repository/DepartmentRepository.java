package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    List<Department> findByActiveTrue();

    List<Department> findByActiveFalse();

    Optional<Department> findByIdAndActiveTrue(Long id);
}