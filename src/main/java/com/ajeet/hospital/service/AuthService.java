package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.LoginRequest;
import com.ajeet.hospital.dto.LoginResponse;
import com.ajeet.hospital.dto.RefreshTokenRequest;
import com.ajeet.hospital.dto.RegisterRequest;
import com.ajeet.hospital.entity.RefreshToken;
import com.ajeet.hospital.entity.Role;
import com.ajeet.hospital.entity.User;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
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

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(Role.PATIENT);

        userRepository.save(user);
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
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return generateLoginResponse(user);
    }


    // =========================================================
    // GOOGLE LOGIN
    // =========================================================

    public LoginResponse googleLogin(
            OAuth2User oauth2User) {

        // Get email from Google
        String email = oauth2User.getAttribute("email");

        if (email == null || email.isBlank()) {

            throw new RuntimeException(
                    "Email not provided by Google"
            );
        }

        // Check whether user already exists
        User user = userRepository
                .findByUsername(email)
                .orElse(null);


        // =====================================================
        // USER DOES NOT EXIST
        // =====================================================

        if (user == null) {

            user = new User();

            // We are using Google email as username
            user.setUsername(email);

            /*
             * Google users don't log in using our password.
             *
             * But our database password column is NOT NULL,
             * therefore we need to store a random encoded
             * password.
             */
            String randomPassword =
                    UUID.randomUUID().toString();

            user.setPassword(
                    passwordEncoder.encode(
                            randomPassword
                    )
            );

            // Google registered users become PATIENT
            user.setRole(Role.PATIENT);

            user = userRepository.save(user);
        }


        // =====================================================
        // USER ALREADY EXISTS
        // =====================================================

        return generateLoginResponse(user);
    }


    // =========================================================
    // GENERATE JWT + REFRESH TOKEN
    // =========================================================

    private LoginResponse generateLoginResponse(
            User user) {

        // Generate access JWT
        String accessToken =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

        // Generate refresh token
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

        User user = newRefreshToken.getUser();

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