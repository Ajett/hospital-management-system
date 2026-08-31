package com.ajeet.hospital.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        UserController controller = new UserController();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(
                        new AuthenticationPrincipalArgumentResolver()
                )
                .build();
    }

    @Test
    void profile_shouldReturnUsername() throws Exception {

        UserDetails userDetails =
                User.withUsername("ajeet@gmail.com")
                        .password("password")
                        .roles("PATIENT")
                        .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        mockMvc.perform(
                        get("/api/users/profile")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string("ajeet@gmail.com")
                );

        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_shouldReturnUsernameForAdmin()
            throws Exception {

        UserDetails userDetails =
                User.withUsername("admin@gmail.com")
                        .password("password")
                        .roles("ADMIN")
                        .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        mockMvc.perform(
                        get("/api/users/profile")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string("admin@gmail.com")
                );

        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_shouldReturnCurrentAuthenticatedUsername()
            throws Exception {

        UserDetails userDetails =
                User.withUsername("patient@gmail.com")
                        .password("password")
                        .roles("PATIENT")
                        .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        mockMvc.perform(
                        get("/api/users/profile")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string("patient@gmail.com")
                );

        SecurityContextHolder.clearContext();
    }

    @Test
    void profile_withoutAuthentication_shouldReturnMessage()
            throws Exception {

        SecurityContextHolder.clearContext();

        mockMvc.perform(
                        get("/api/users/profile")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string("User not authenticated")
                );
    }

    @Test
    void profile_shouldReturnDoctorUsername()
            throws Exception {

        UserDetails userDetails =
                User.withUsername("doctor@gmail.com")
                        .password("password")
                        .roles("DOCTOR")
                        .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        mockMvc.perform(
                        get("/api/users/profile")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string("doctor@gmail.com")
                );

        SecurityContextHolder.clearContext();
    }
}