package com.ajeet.hospital.service;

import com.ajeet.hospital.entity.Role;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        userDetailsService =
                new CustomUserDetailsService(
                        userRepository
                );
    }


    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {

        // Given
        User user = new User();

        user.setUsername("ajeet");
        user.setPassword("encoded-password");
        user.setRole(Role.PATIENT);

        when(userRepository.findByUsername("ajeet"))
                .thenReturn(Optional.of(user));


        // When
        UserDetails result =
                userDetailsService.loadUserByUsername("ajeet");


        // Then
        assertNotNull(result);

        assertEquals(
                "ajeet",
                result.getUsername()
        );

        assertEquals(
                "encoded-password",
                result.getPassword()
        );

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_PATIENT")
                        )
        );

        verify(userRepository)
                .findByUsername("ajeet");
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown")
        );

        assertEquals(
                "User not found: unknown",
                exception.getMessage()
        );

        verify(userRepository)
                .findByUsername("unknown");
    }
}
