package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.PublicHospitalResponse;
import com.ajeet.hospital.entity.Hospital;
import com.ajeet.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    // =========================================================
    // PUBLIC
    // =========================================================

    public List<PublicHospitalResponse> getPublicHospitals() {

        return hospitalRepository.findByActiveTrue()
                .stream()
                .map(this::toPublicResponse)
                .toList();
    }

    public PublicHospitalResponse getPublicHospital(Long id) {

        Hospital hospital =
                hospitalRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Hospital not found"
                                )
                        );

        return toPublicResponse(hospital);
    }

    public List<PublicHospitalResponse> searchPublicHospitals(
            String query,
            String location) {

        return hospitalRepository
                .searchPublicHospitals(query, location)
                .stream()
                .map(this::toPublicResponse)
                .toList();
    }

    // =========================================================
    // ADMIN
    // =========================================================

    public List<Hospital> getAllHospitals() {

        return hospitalRepository.findByActiveTrue();
    }

    public Hospital getHospitalById(Long id) {

        return hospitalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Hospital not found"
                        )
                );
    }

    public Hospital createHospital(Hospital hospital) {

        // New hospitals are active by default.
        hospital.setActive(true);

        return hospitalRepository.save(hospital);
    }

    public Hospital updateHospital(
            Long id,
            Hospital hospital) {

        Hospital existingHospital =
                hospitalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Hospital not found"
                                )
                        );

        existingHospital.setName(
                hospital.getName()
        );

        existingHospital.setLocation(
                hospital.getLocation()
        );

        existingHospital.setAddress(
                hospital.getAddress()
        );

        existingHospital.setPhone(
                hospital.getPhone()
        );

        return hospitalRepository.save(existingHospital);
    }

    // =========================================================
    // SOFT DELETE
    // =========================================================

    public void deleteHospital(Long id) {

        Hospital hospital =
                hospitalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Hospital not found"
                                )
                        );

        hospital.setActive(false);

        hospitalRepository.save(hospital);
    }

    // =========================================================
    // RESTORE
    // =========================================================

    public void restoreHospital(Long id) {

        Hospital hospital =
                hospitalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Hospital not found"
                                )
                        );

        hospital.setActive(true);

        hospitalRepository.save(hospital);
    }

    // =========================================================
    // PUBLIC DTO MAPPER
    // =========================================================

    private PublicHospitalResponse toPublicResponse(
            Hospital hospital) {

        PublicHospitalResponse response =
                new PublicHospitalResponse();

        response.setId(hospital.getId());
        response.setName(hospital.getName());
        response.setLocation(hospital.getLocation());
        response.setAddress(hospital.getAddress());
        response.setPhone(hospital.getPhone());

        return response;
    }
}