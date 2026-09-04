package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.*;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.entity.RefreshToken;
import com.ajeet.hospital.entity.Role;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.PasswordResetTokenRepository;
import com.ajeet.hospital.repository.PatientRepository;
import com.ajeet.hospital.repository.UserRepository;
import com.ajeet.hospital.entity.PasswordResetToken;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;


    private final String frontendUrl;

    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            @Value("${frontend.url}") String frontendUrl) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepository =
                passwordResetTokenRepository;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
    }


    // =========================================================
    // NORMAL REGISTER
    // =========================================================

    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(
                request.getUsername()
        ).isPresent()) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }


        // ============================================
        // CREATE USER
        // ============================================

        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(
                Role.PATIENT
        );

        user = userRepository.save(user);


        // ============================================
        // CREATE PATIENT PROFILE
        // ============================================

        Patient patient = new Patient();

        patient.setName(
                request.getUsername()
        );

        patient.setUser(user);

        patientRepository.save(patient);
    }


    // =========================================================
    // NORMAL LOGIN
    // =========================================================

    public LoginResponse login(LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

        } catch (BadCredentialsException e) {

            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }

        User user = userRepository
                .findByUsername(
                        request.getUsername()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        return generateLoginResponse(user);
    }


    // =========================================================
    // GOOGLE LOGIN
    // =========================================================

    public LoginResponse googleLogin(
            OAuth2User oauth2User) {

        String email =
                oauth2User.getAttribute("email");

        if (email == null || email.isBlank()) {

            throw new RuntimeException(
                    "Email not provided by Google"
            );
        }


        User user =
                userRepository
                        .findByUsername(email)
                        .orElse(null);


        // ============================================
        // NEW GOOGLE USER
        // ============================================

        if (user == null) {

            user = new User();

            user.setUsername(email);

            String randomPassword =
                    UUID.randomUUID().toString();

            user.setPassword(
                    passwordEncoder.encode(
                            randomPassword
                    )
            );

            user.setRole(
                    Role.PATIENT
            );

            user =
                    userRepository.save(user);


            // ========================================
            // CREATE PATIENT PROFILE
            // ========================================

            Patient patient =
                    new Patient();

            patient.setName(email);

            patient.setUser(user);

            patientRepository.save(patient);
        }


        // ============================================
        // LOGIN
        // ============================================

        return generateLoginResponse(user);
    }


    // =========================================================
    // GENERATE JWT + REFRESH TOKEN
    // =========================================================

    private LoginResponse generateLoginResponse(
            User user) {

        String accessToken =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getUsername()
                );

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                user.getUsername(),
                user.getRole().name()
        );
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    public LoginResponse refreshToken(
            RefreshTokenRequest request) {

        RefreshToken newRefreshToken =
                refreshTokenService.rotateRefreshToken(
                        request.getRefreshToken()
                );

        User user =
                newRefreshToken.getUser();

        return generateLoginResponse(user);
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    public void logout(String refreshToken) {

        refreshTokenService.deleteByToken(
                refreshToken
        );
    }

    public void changePassword(
            String username,
            ChangePasswordRequest request) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );


        // Current password verify
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }


        // New password confirmation
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "New password and confirm password do not match"
            );
        }


        // Prevent same password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "New password must be different from current password"
            );
        }


        // Save encoded password
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    // =========================================================
// FORGOT PASSWORD
// =========================================================

    public void forgotPassword(
            ForgotPasswordRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return;
        }

        passwordResetTokenRepository
                .deleteByUserId(user.getId());

        String token =
                UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(
                resetToken
        );

        String resetLink =
                frontendUrl +
                        "/reset-password?token=" +
                        token;

        emailService.sendPasswordResetEmail(
                email,
                resetLink
        );
    }

    // =========================================================
// RESET PASSWORD
// =========================================================

    public void resetPassword(
            ResetPasswordRequest request) {

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired reset link"
                                )
                        );

        if (resetToken.isUsed()) {

            throw new IllegalArgumentException(
                    "This reset link has already been used"
            );
        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "This reset link has expired"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        resetToken.setUsed(true);

        passwordResetTokenRepository.save(
                resetToken
        );
    }
}