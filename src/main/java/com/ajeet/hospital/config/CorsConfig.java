package com.ajeet.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // ============================================
        // ALLOWED FRONTEND ORIGINS
        // ============================================

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174"
        ));


        // ============================================
        // ALLOWED HTTP METHODS
        // ============================================

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));


        // ============================================
        // ALLOWED HEADERS
        // ============================================

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));


        // ============================================
        // CREDENTIALS
        // ============================================

        configuration.setAllowCredentials(true);


        // ============================================
        // REGISTER CORS CONFIGURATION
        // ============================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}