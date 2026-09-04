package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.UserProfileRequest;
import com.ajeet.hospital.dto.UserProfileResponse;
import com.ajeet.hospital.service.UserProfileService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(
            UserProfileService userProfileService) {

        this.userProfileService =
                userProfileService;
    }


    // =========================================================
    // GET LOGGED-IN USER PROFILE
    // =========================================================

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(
            Authentication authentication) {

        String username =
                authentication.getName();

        return userProfileService.getMyProfile(
                username
        );
    }


    // =========================================================
    // UPDATE LOGGED-IN USER PROFILE
    // =========================================================

    @PutMapping("/me")
    public UserProfileResponse updateMyProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication) {

        String username =
                authentication.getName();

        return userProfileService.updateMyProfile(
                username,
                request
        );
    }
}