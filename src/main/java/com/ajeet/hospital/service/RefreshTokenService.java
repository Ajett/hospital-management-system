package com.ajeet.hospital.service;

import com.ajeet.hospital.entity.RefreshToken;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.RefreshTokenRepository;
import com.ajeet.hospital.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setExpiryDate(
                Instant.now().plusSeconds(7 * 24 * 60 * 60)
        );

        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiryDate()
                .isBefore(Instant.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    public RefreshToken getByToken(String token) {

        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));
    }

    public void deleteByToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                ));

        refreshTokenRepository.delete(refreshToken);
    }

    public RefreshToken rotateRefreshToken(String oldToken) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(oldToken)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                ));

        verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        refreshTokenRepository.delete(refreshToken);

        RefreshToken newRefreshToken = new RefreshToken();

        newRefreshToken.setToken(
                UUID.randomUUID().toString()
        );

        newRefreshToken.setExpiryDate(
                Instant.now().plusSeconds(7 * 24 * 60 * 60)
        );

        newRefreshToken.setUser(user);

        return refreshTokenRepository.save(newRefreshToken);
    }
}
