package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.UserResponse;
import com.ajeet.hospital.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Existing profile endpoint
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "User not authenticated";
        }

        return userDetails.getUsername();
    }

    // Get all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get user by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Disable user
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public String disableUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.disableUser(id, userDetails.getUsername());

        return "User disabled successfully";
    }

    // Enable user
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/enable")
    public String enableUser(@PathVariable Long id) {

        userService.enableUser(id);

        return "User enabled successfully";
    }
}