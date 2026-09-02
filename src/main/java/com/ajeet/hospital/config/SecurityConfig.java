package com.ajeet.hospital.config;

import com.ajeet.hospital.security.RestAccessDeniedHandler;
import com.ajeet.hospital.security.RestAuthenticationEntryPoint;
import com.ajeet.hospital.service.CustomUserDetailsService;
import com.ajeet.hospital.service.JwtAuthenticationFilter;
import com.ajeet.hospital.service.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private final RestAccessDeniedHandler accessDeniedHandler;


    public SecurityConfig(
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) {

        this.authenticationEntryPoint =
                authenticationEntryPoint;

        this.accessDeniedHandler =
                accessDeniedHandler;
    }


    // ============================================
    // Password Encoder
    // ============================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ============================================
    // Authentication Manager
    // ============================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }


    // ============================================
    // Security Filter Chain
    // ============================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService)
            throws Exception {


        // Create JWT filter
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );


        http

                // ====================================
                // CSRF
                // ====================================

                .csrf(csrf -> csrf.disable())

                // ====================================
                // CORS
                // ====================================

                .cors(cors -> {})


                // ====================================
                // SESSION
                // ====================================

                /*
                 * IMPORTANT:
                 *
                 * OAuth2 login needs a session during
                 * the Google authentication process.
                 *
                 * Therefore ,DON'T use STATELESS here.
                 */

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )


                // ====================================
                // AUTHORIZATION
                // ====================================

                .authorizeHttpRequests(auth -> auth


                        // ------------------------------
                        // Your authentication APIs
                        // ------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // ------------------------------
                        // OAuth2 authorization
                        // ------------------------------

                        .requestMatchers(
                                "/oauth2/**"
                        ).permitAll()


                        // ------------------------------
                        // OAuth2 callback
                        // ------------------------------

                        .requestMatchers(
                                "/login/**"
                        ).permitAll()


                        // ------------------------------
                        // Error
                        // ------------------------------

                        .requestMatchers(
                                "/error"
                        ).permitAll()


                        // ------------------------------
                        // Everything else
                        // ------------------------------

                        .anyRequest().authenticated()
                )


                // ====================================
                // EXCEPTION HANDLING
                // ====================================

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                authenticationEntryPoint
                        )

                        .accessDeniedHandler(
                                accessDeniedHandler
                        )
                )


                // ====================================
                // GOOGLE OAUTH2 LOGIN
                // ====================================

                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/auth/oauth2/success", true)
                        .failureUrl("/api/auth/oauth2/failure")
                )


                // ====================================
                // JWT FILTER
                // ====================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}