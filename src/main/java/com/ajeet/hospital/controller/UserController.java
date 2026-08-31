package com.ajeet.hospital.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "User not authenticated";
        }

        return userDetails.getUsername();
    }
}