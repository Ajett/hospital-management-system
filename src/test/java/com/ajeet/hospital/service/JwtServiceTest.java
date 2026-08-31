package com.ajeet.hospital.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
public class JwtServiceTest {
    private JwtService jwtService;

    private final String secret =
            "DNM7SC6d39ISWFPBiQpUHuimwSMDlnO0AXGvwE70n7U=";

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                secret
        );
    }

    @Test
    void generateToken_shouldCreateValidJwt() {

        String token =
                jwtService.generateToken(
                        "ajeet@gmail.com",
                        "PATIENT"
                );

        assertNotNull(token);

        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldReturnUsernameFromToken() {

        String token =
                jwtService.generateToken(
                        "ajeet@gmail.com",
                        "PATIENT"
                );

        String username =
                jwtService.extractUsername(token);

        assertEquals(
                "ajeet@gmail.com",
                username
        );
    }

    @Test
    void isTokenValid_shouldReturnTrueForCorrectUsername() {

        String token =
                jwtService.generateToken(
                        "ajeet@gmail.com",
                        "PATIENT"
                );

        boolean result =
                jwtService.isTokenValid(
                        token,
                        "ajeet@gmail.com"
                );

        assertTrue(result);
    }

    @Test
    void isTokenValid_shouldReturnFalseForWrongUsername() {

        String token =
                jwtService.generateToken(
                        "ajeet@gmail.com",
                        "PATIENT"
                );

        boolean result =
                jwtService.isTokenValid(
                        token,
                        "wrong@gmail.com"
                );

        assertFalse(result);
    }

    @Test
    void extractUsername_shouldThrowExceptionForInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        assertThrows(
                Exception.class,
                () -> jwtService.extractUsername(invalidToken)
        );
    }

    @Test
    void isTokenValid_shouldThrowExceptionForInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        assertThrows(
                Exception.class,
                () -> jwtService.isTokenValid(
                        invalidToken,
                        "ajeet@gmail.com"
                )
        );
    }
}
