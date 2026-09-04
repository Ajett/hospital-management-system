package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.UserProfileRequest;
import com.ajeet.hospital.dto.UserProfileResponse;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.PatientRepository;
import com.ajeet.hospital.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public UserProfileService(
            UserRepository userRepository,
            PatientRepository patientRepository) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    // =========================
    // GET MY PROFILE
    // =========================

    public UserProfileResponse getMyProfile(
            String username) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + username
                                )
                        );

        UserProfileResponse response =
                new UserProfileResponse();

        response.setUserId(
                user.getId()
        );

        response.setUsername(
                user.getUsername()
        );

        response.setRole(
                user.getRole().name()
        );

        response.setName(
                user.getName()
        );

        response.setEmail(
                user.getEmail()
        );

        response.setPhone(
                user.getPhone()
        );

        // =========================
        // PATIENT PROFILE
        // =========================

        if ("PATIENT".equals(
                user.getRole().name())) {

            Patient patient =
                    getOrCreatePatient(user);

            response.setPatientId(
                    patient.getId()
            );

            response.setDateOfBirth(
                    patient.getDateOfBirth()
            );

            response.setGender(
                    patient.getGender()
            );

            response.setAddress(
                    patient.getAddress()
            );

            // Fallback for old records
            if (response.getName() == null) {
                response.setName(
                        patient.getName()
                );
            }

            if (response.getEmail() == null) {
                response.setEmail(
                        patient.getEmail()
                );
            }

            if (response.getPhone() == null) {
                response.setPhone(
                        patient.getPhone()
                );
            }
        }

        return response;
    }

    // =========================
    // UPDATE MY PROFILE
    // =========================

    public UserProfileResponse updateMyProfile(
            String currentUsername,
            UserProfileRequest request) {

        User user =
                userRepository
                        .findByUsername(currentUsername)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + currentUsername
                                )
                        );

        Patient patient = null;

        // Create/get patient BEFORE username update
        if ("PATIENT".equals(
                user.getRole().name())) {

            patient = getOrCreatePatient(user);
        }

        // =========================
        // USERNAME
        // =========================

        if (!user.getUsername()
                .equals(request.getUsername())) {

            boolean usernameExists =
                    userRepository
                            .existsByUsername(
                                    request.getUsername()
                            );

            if (usernameExists) {

                throw new RuntimeException(
                        "Username already exists: "
                                + request.getUsername()
                );
            }

            user.setUsername(
                    request.getUsername()
            );
        }

        // =========================
        // USER PROFILE
        // =========================

        if (request.getName() != null) {

            user.setName(
                    request.getName()
            );
        }

        if (request.getEmail() != null) {

            user.setEmail(
                    request.getEmail()
            );
        }

        if (request.getPhone() != null) {

            user.setPhone(
                    request.getPhone()
            );
        }

        userRepository.save(user);

        // =========================
        // PATIENT PROFILE
        // =========================

        if (patient != null) {

            if (request.getDateOfBirth() != null) {

                patient.setDateOfBirth(
                        request.getDateOfBirth()
                );
            }

            if (request.getGender() != null) {

                patient.setGender(
                        request.getGender()
                );
            }

            if (request.getAddress() != null) {

                patient.setAddress(
                        request.getAddress()
                );
            }

            // Keep old Patient fields synchronized

            if (request.getName() != null) {

                patient.setName(
                        request.getName()
                );
            }

            if (request.getEmail() != null) {

                patient.setEmail(
                        request.getEmail()
                );
            }

            if (request.getPhone() != null) {

                patient.setPhone(
                        request.getPhone()
                );
            }

            patientRepository.save(patient);
        }

        return getMyProfile(
                user.getUsername()
        );
    }

    // =========================
    // GET OR CREATE PATIENT
    // =========================

    private Patient getOrCreatePatient(
            User user) {

        return patientRepository
                .findByUserUsername(
                        user.getUsername()
                )
                .orElseGet(() -> {

                    Patient patient =
                            new Patient();

                    patient.setUser(user);

                    if (user.getName() != null) {

                        patient.setName(
                                user.getName()
                        );

                    } else {

                        patient.setName(
                                user.getUsername()
                        );
                    }

                    if (user.getEmail() != null) {

                        patient.setEmail(
                                user.getEmail()
                        );
                    }

                    if (user.getPhone() != null) {

                        patient.setPhone(
                                user.getPhone()
                        );
                    }

                    return patientRepository.save(
                            patient
                    );
                });
    }
}