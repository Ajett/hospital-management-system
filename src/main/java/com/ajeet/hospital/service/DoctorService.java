package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.DoctorRequest;
import com.ajeet.hospital.dto.DoctorResponse;
import com.ajeet.hospital.dto.PublicDoctorResponse;
import com.ajeet.hospital.entity.Department;
import com.ajeet.hospital.entity.Doctor;
import com.ajeet.hospital.exception.DepartmentNotFoundException;
import com.ajeet.hospital.exception.DoctorNotFoundException;
import com.ajeet.hospital.repository.DepartmentRepository;
import com.ajeet.hospital.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    public DoctorResponse createDoctor(DoctorRequest request) {

        Department department =
                departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() ->
                                new DepartmentNotFoundException(
                                        "Department with id: "
                                                + request.getDepartmentId()
                                                + " not found."
                                )
                        );

        Doctor doctor = new Doctor();

        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPhone(request.getPhone());

        department.addDoctor(doctor);
        doctor.setDepartment(department);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return convertToResponse(savedDoctor);
    }

    public List<DoctorResponse> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    public DoctorResponse getDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor with id " + id + " not found"
                        )
                );

        return convertToResponse(doctor);
    }
    public DoctorResponse updateDoctor(
            Long id,
            DoctorRequest request) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor with id " + id + " not found"
                        )
                );

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new DepartmentNotFoundException(
                                "Department with id "
                                        + request.getDepartmentId()
                                        + " not found"
                        )
                );

        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPhone(request.getPhone());

        department.addDoctor(doctor);
        doctor.setDepartment(department);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return convertToResponse(savedDoctor);
    }

    public void deleteDoctor(Long id) {

        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException(
                    "Doctor with id " + id + " not found"
            );
        }

        doctorRepository.deleteById(id);
    }


    public List<DoctorResponse> findBySpecialization(
            String specialization) {

        return doctorRepository
                .findBySpecialization(specialization)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<DoctorResponse> findBySpecializationAndDepartment(
            String specialization,
            Long departmentId) {

        return doctorRepository
                .findBySpecializationAndDepartment(
                        specialization,
                        departmentId
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private DoctorResponse convertToResponse(Doctor doctor) {

        DoctorResponse response = new DoctorResponse();

        response.setId(doctor.getId());
        response.setName(doctor.getName());
        response.setSpecialization(doctor.getSpecialization());
        response.setPhone(doctor.getPhone());

        if (doctor.getDepartment() != null) {

            response.setDepartmentId(
                    doctor.getDepartment().getId()
            );

            response.setDepartmentName(
                    doctor.getDepartment().getName()
            );
        }

        return response;
    }

    public List<DoctorResponse> searchByNameAndDepartment(
            String name,
            Long departmentId) {

        return doctorRepository
                .searchByNameAndDepartment(name, departmentId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public Page<DoctorResponse> searchDoctors(
            String specialization,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<Doctor> doctors =
                doctorRepository.searchDoctors(
                        specialization,
                        pageable
                );

        return doctors.map(this::convertToResponse);
    }

    // =========================================================
    // PUBLIC DOCTOR APIs
    // =========================================================

    public List<PublicDoctorResponse> getPublicDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(this::convertToPublicResponse)
                .toList();
    }


    public PublicDoctorResponse getPublicDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor with id " + id + " not found"
                        )
                );

        return convertToPublicResponse(doctor);
    }


    public List<PublicDoctorResponse> searchPublicDoctors(
            String query,
            String specialization,
            String location) {

        return doctorRepository
                .searchPublicDoctors(
                        query,
                        specialization,
                        location
                )
                .stream()
                .map(this::convertToPublicResponse)
                .toList();
    }


    private PublicDoctorResponse convertToPublicResponse(
            Doctor doctor) {

        PublicDoctorResponse response =
                new PublicDoctorResponse();

        response.setId(doctor.getId());
        response.setName(doctor.getName());
        response.setSpecialization(
                doctor.getSpecialization()
        );

        if (doctor.getDepartment() != null) {

            response.setDepartmentName(
                    doctor.getDepartment().getName()
            );

            response.setLocation(
                    doctor.getDepartment().getLocation()
            );
        }

        return response;
    }
}
