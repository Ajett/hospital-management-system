package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.*;
import com.ajeet.hospital.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return "User registered successfully";
    }



    // =========================================================
    // NORMAL LOGIN
    // =========================================================

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @PostMapping("/refresh")
    public LoginResponse refreshToken(
            @RequestBody RefreshTokenRequest request) {

        return authService.refreshToken(request);
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public String logout(
          @Valid  @RequestBody RefreshTokenRequest request) {

        authService.logout(
                request.getRefreshToken()
        );

        return "Logout successful";
    }


    // =========================================================
    // GOOGLE LOGIN SUCCESS
    // =========================================================

    @GetMapping("/oauth2/success")
    public LoginResponse oauth2Success(
            @AuthenticationPrincipal OAuth2User oauth2User) {

        return authService.googleLogin(
                oauth2User
        );
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<?> oauth2Failure() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "message", "Google login failed"
                ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        authService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password changed successfully"
                )
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "If an account exists with this email, " +
                                "a password reset link has been sent."
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully"
                )
        );
    }
}