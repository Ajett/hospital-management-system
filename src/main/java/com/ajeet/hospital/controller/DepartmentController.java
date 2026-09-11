package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.DepartmentRequest;
import com.ajeet.hospital.dto.DepartmentResponse;
import com.ajeet.hospital.entity.Department;
import com.ajeet.hospital.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService) {

        this.departmentService = departmentService;
    }

    // =========================================================
    // ADMIN - CREATE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Department createDepartment(
            @Valid @RequestBody DepartmentRequest request) {

        return departmentService.createDepartment(request);
    }

    // =========================================================
    // ADMIN - GET ALL ACTIVE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Department> getAllDepartments() {

        return departmentService.getAllDepartments();
    }

    // =========================================================
    // ADMIN - GET ACTIVE DEPARTMENT BY ID
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public DepartmentResponse getDepartmentById(
            @PathVariable Long id) {

        return departmentService.getDepartmentWithDoctors(id);
    }

    // =========================================================
    // ADMIN - UPDATE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Department updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        return departmentService.updateDepartment(
                id,
                request
        );
    }

    // =========================================================
    // ADMIN - SOFT DELETE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return "Department deleted successfully";
    }

    // =========================================================
    // ADMIN - RESTORE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public String restoreDepartment(
            @PathVariable Long id) {

        departmentService.restoreDepartment(id);

        return "Department restored successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<DepartmentResponse> getAllDepartmentsForAdmin() {
        return departmentService.getAllDepartmentsForAdmin();
    }
}