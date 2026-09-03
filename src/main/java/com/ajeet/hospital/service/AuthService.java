package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.LoginRequest;
import com.ajeet.hospital.dto.LoginResponse;
import com.ajeet.hospital.dto.RefreshTokenRequest;
import com.ajeet.hospital.dto.RegisterRequest;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.entity.RefreshToken;
import com.ajeet.hospital.entity.Role;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.PatientRepository;
import com.ajeet.hospital.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
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


    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

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
}