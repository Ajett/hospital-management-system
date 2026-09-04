package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.UserProfileRequest;
import com.ajeet.hospital.dto.UserProfileResponse;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.exception.PatientNotFoundException;
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

    public UserProfileResponse getMyProfile(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + username
                        )
                );

        UserProfileResponse response =
                new UserProfileResponse();

        // Generic User information
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());

        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        // Patient-specific information
        if ("PATIENT".equals(user.getRole().name())) {

            patientRepository
                    .findByUserUsername(username)
                    .ifPresent(patient -> {

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

                        // Fallback for old patient records
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
                    });
        }

        return response;
    }

    // =========================
    // UPDATE MY PROFILE
    // =========================

    public UserProfileResponse updateMyProfile(
            String currentUsername,
            UserProfileRequest request) {

        User user = userRepository
                .findByUsername(currentUsername)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: "
                                        + currentUsername
                        )
                );

        /*
         * IMPORTANT:
         * Load patient BEFORE username change.
         *
         * Otherwise, findByUserUsername(currentUsername)
         * may fail after username is changed.
         */
        Patient patient = null;

        if ("PATIENT".equals(user.getRole().name())) {

            patient = patientRepository
                    .findByUserUsername(currentUsername)
                    .orElseThrow(() ->
                            new PatientNotFoundException(
                                    "Patient profile not found for user "
                                            + currentUsername
                            )
                    );
        }

        // =========================
        // USERNAME
        // =========================

        if (!user.getUsername().equals(request.getUsername())) {

            boolean usernameExists =
                    userRepository.existsByUsername(
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
        // GENERIC USER PROFILE
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

            /*
             * Keep old Patient fields synchronized.
             * This helps existingAPIs that still use
             * Patient.name/email/phone.
             */

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

        // Return updated profile
        return getMyProfile(
                user.getUsername()
        );
    }
}