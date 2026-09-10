package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.DepartmentRequest;
import com.ajeet.hospital.dto.DepartmentResponse;
import com.ajeet.hospital.entity.Department;
import com.ajeet.hospital.exception.DepartmentNotFoundException;
import com.ajeet.hospital.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(
            DepartmentRepository departmentRepository) {

        this.departmentRepository = departmentRepository;
    }

    // =========================================================
    // CREATE
    // =========================================================

    public Department createDepartment(
            DepartmentRequest request) {

        Department department = new Department();

        department.setName(request.getName());
        department.setLocation(request.getLocation());
        department.setActive(true);

        return departmentRepository.save(department);
    }

    // =========================================================
    // GET ALL ACTIVE DEPARTMENTS
    // =========================================================

    public List<Department> getAllDepartments() {

        return departmentRepository.findByActiveTrue();
    }

    // =========================================================
    // GET ACTIVE DEPARTMENT WITH DOCTORS
    // =========================================================

    public DepartmentResponse getDepartmentWithDoctors(
            Long id) {

        Department department =
                departmentRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new DepartmentNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        DepartmentResponse response =
                new DepartmentResponse();

        response.setId(department.getId());
        response.setName(department.getName());
        response.setLocation(department.getLocation());

        List<DepartmentResponse.DoctorSummary> doctors =
                department.getDoctors()
                        .stream()
                        .filter(doctor -> doctor.isActive())
                        .map(doctor -> {

                            DepartmentResponse.DoctorSummary summary =
                                    new DepartmentResponse.DoctorSummary();

                            summary.setId(doctor.getId());
                            summary.setName(doctor.getName());
                            summary.setSpecialization(
                                    doctor.getSpecialization()
                            );

                            return summary;
                        })
                        .toList();

        response.setDoctors(doctors);

        return response;
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public Department updateDepartment(
            Long id,
            DepartmentRequest request) {

        Department existingDepartment =
                departmentRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new DepartmentNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        existingDepartment.setName(
                request.getName()
        );

        existingDepartment.setLocation(
                request.getLocation()
        );

        return departmentRepository.save(
                existingDepartment
        );
    }

    // =========================================================
    // SOFT DELETE
    // =========================================================

    public void deleteDepartment(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new DepartmentNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        department.setActive(false);

        departmentRepository.save(department);
    }

    // =========================================================
    // RESTORE
    // =========================================================

    public void restoreDepartment(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new DepartmentNotFoundException(
                                        "Department with id "
                                                + id
                                                + " not found"
                                )
                        );

        department.setActive(true);

        departmentRepository.save(department);
    }
}