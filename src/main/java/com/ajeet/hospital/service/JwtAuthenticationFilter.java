package com.ajeet.hospital.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        System.out.println("================================");
        System.out.println("JWT FILTER CALLED");
        System.out.println("REQUEST URL = " + requestUri);
        System.out.println("AUTH HEADER = "
                + request.getHeader("Authorization"));
        System.out.println("================================");


        // ============================================
        // IMPORTANT:
        // Do NOT process Google OAuth2 requests
        // ============================================

        if (requestUri.startsWith("/oauth2/")
                || requestUri.startsWith("/login/")) {

            filterChain.doFilter(request, response);
            return;
        }


        // ============================================
        // Get Authorization header
        // ============================================

        String authHeader =
                request.getHeader("Authorization");


        // No JWT → simply continue
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        // ============================================
        // Extract JWT
        // ============================================

        String token = authHeader.substring(7);


        try {

            // Extract username
            String username =
                    jwtService.extractUsername(token);


            // Load user
            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(username);


            // Validate JWT
            if (jwtService.isTokenValid(
                    token,
                    userDetails.getUsername())) {


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                // Set authentication
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }


        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid or expired JWT"
                    }
                    """);

            return;
        }


        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}