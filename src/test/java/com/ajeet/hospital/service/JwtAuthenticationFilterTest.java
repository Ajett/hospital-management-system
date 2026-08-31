package com.ajeet.hospital.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        // Clear authentication before every test
        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // TEST 1
    // No Authorization header
    // =========================================================

    @Test
    void doFilter_shouldContinueWhenAuthorizationHeaderMissing()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/patients");

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }


    // =========================================================
    // TEST 2
    // Authorization header is not Bearer
    // =========================================================

    @Test
    void doFilter_shouldContinueWhenAuthorizationHeaderIsNotBearer()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/patients");

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }


    // =========================================================
    // TEST 3
    // OAuth2 request
    // =========================================================

    @Test
    void doFilter_shouldSkipJwtForOAuth2Request()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/oauth2/authorization/google");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }


    // =========================================================
    // TEST 4
    // Login request
    // =========================================================

    @Test
    void doFilter_shouldSkipJwtForLoginRequest()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/login/oauth2/code/google");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }


    // =========================================================
    // TEST 5
    // Valid JWT
    // =========================================================

    @Test
    void doFilter_shouldAuthenticateUserWhenJwtIsValid()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/patients");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer valid-token");

        when(jwtService.extractUsername("valid-token"))
                .thenReturn("ajeet@gmail.com");

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("ajeet@gmail.com")
                        .password("password")
                        .authorities("ROLE_PATIENT")
                        .build();

        when(userDetailsService.loadUserByUsername(
                "ajeet@gmail.com"
        )).thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "valid-token",
                "ajeet@gmail.com"
        )).thenReturn(true);


        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );


        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        assertEquals(
                "ajeet@gmail.com",
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );


        verify(jwtService)
                .extractUsername("valid-token");

        verify(userDetailsService)
                .loadUserByUsername("ajeet@gmail.com");

        verify(jwtService)
                .isTokenValid(
                        "valid-token",
                        "ajeet@gmail.com"
                );

        verify(filterChain)
                .doFilter(request, response);


        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // TEST 6
    // Invalid JWT
    // =========================================================

    @Test
    void doFilter_shouldReturn401WhenJwtIsInvalid()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/patients");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer invalid-token");

        when(jwtService.extractUsername("invalid-token"))
                .thenThrow(
                        new RuntimeException("Invalid JWT")
                );


        PrintWriter writer = mock(PrintWriter.class);

        when(response.getWriter())
                .thenReturn(writer);


        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );


        verify(response)
                .setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

        verify(response)
                .setContentType("application/json");


        verify(writer)
                .write("""
                        {
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "Invalid or expired JWT"
                        }
                        """);


        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotAuthenticateUserWhenJwtIsInvalid()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/patients");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer invalid-user-token");

        when(jwtService.extractUsername("invalid-user-token"))
                .thenReturn("ajeet@gmail.com");

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("ajeet@gmail.com")
                        .password("password")
                        .authorities("ROLE_PATIENT")
                        .build();

        when(userDetailsService.loadUserByUsername(
                "ajeet@gmail.com"
        )).thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "invalid-user-token",
                "ajeet@gmail.com"
        )).thenReturn(false);


        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );


        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );


        verify(jwtService)
                .extractUsername("invalid-user-token");

        verify(userDetailsService)
                .loadUserByUsername("ajeet@gmail.com");

        verify(jwtService)
                .isTokenValid(
                        "invalid-user-token",
                        "ajeet@gmail.com"
                );

        verify(filterChain)
                .doFilter(request, response);


        SecurityContextHolder.clearContext();
    }


}