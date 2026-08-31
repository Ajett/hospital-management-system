package com.ajeet.hospital.service;
import com.ajeet.hospital.entity.RefreshToken;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.RefreshTokenRepository;
import com.ajeet.hospital.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void createRefreshToken_shouldCreateAndSaveToken() {

        User user = new User();
        user.setUsername("ajeet");

        when(userRepository.findByUsername("ajeet"))
                .thenReturn(Optional.of(user));

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result =
                refreshTokenService.createRefreshToken("ajeet");

        assertNotNull(result);

        assertNotNull(result.getToken());

        assertEquals(
                user,
                result.getUser()
        );

        assertNotNull(
                result.getExpiryDate()
        );

        verify(userRepository)
                .findByUsername("ajeet");

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.createRefreshToken("unknown")
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByUsername("unknown");

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_shouldReturnTokenWhenTokenIsValid() {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken("valid-token");

        refreshToken.setExpiryDate(
                Instant.now().plusSeconds(3600)
        );

        RefreshToken result =
                refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);

        assertEquals(
                refreshToken,
                result
        );

        verify(refreshTokenRepository, never())
                .delete(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_shouldDeleteAndThrowExceptionWhenTokenExpired() {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken("expired-token");

        refreshToken.setExpiryDate(
                Instant.now().minusSeconds(3600)
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.verifyExpiration(refreshToken)
        );

        assertEquals(
                "Refresh token has expired",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .delete(refreshToken);
    }

    @Test
    void getByToken_shouldReturnTokenWhenTokenExists() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("my-refresh-token");

        when(refreshTokenRepository.findByToken("my-refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.getByToken("my-refresh-token");

        assertNotNull(result);

        assertEquals(
                "my-refresh-token",
                result.getToken()
        );

        assertEquals(
                refreshToken,
                result
        );

        verify(refreshTokenRepository)
                .findByToken("my-refresh-token");
    }

    @Test
    void getByToken_shouldThrowExceptionWhenTokenNotFound() {

        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.getByToken("invalid-token")
        );

        assertEquals(
                "Refresh token not found",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("invalid-token");
    }

    @Test
    void deleteByToken_shouldDeleteTokenWhenTokenExists() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("my-refresh-token");

        when(refreshTokenRepository.findByToken("my-refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        refreshTokenService.deleteByToken("my-refresh-token");

        verify(refreshTokenRepository)
                .findByToken("my-refresh-token");

        verify(refreshTokenRepository)
                .delete(refreshToken);
    }

    @Test
    void deleteByToken_shouldThrowExceptionWhenTokenNotFound() {

        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.deleteByToken("invalid-token")
        );

        assertEquals(
                "Refresh token not found",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("invalid-token");

        verify(refreshTokenRepository, never())
                .delete(any(RefreshToken.class));
    }

    @Test
    void rotateRefreshToken_shouldDeleteOldAndCreateNewToken() {

        // Given
        User user = new User();
        user.setUsername("ajeet");

        RefreshToken oldToken = new RefreshToken();

        oldToken.setToken("old-token");

        oldToken.setExpiryDate(
                Instant.now().plusSeconds(3600)
        );

        oldToken.setUser(user);

        when(refreshTokenRepository.findByToken("old-token"))
                .thenReturn(Optional.of(oldToken));

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // When
        RefreshToken newToken =
                refreshTokenService.rotateRefreshToken("old-token");


        // Then
        assertNotNull(newToken);

        assertNotNull(newToken.getToken());

        assertNotEquals(
                "old-token",
                newToken.getToken()
        );

        assertEquals(
                user,
                newToken.getUser()
        );

        assertNotNull(
                newToken.getExpiryDate()
        );

        verify(refreshTokenRepository)
                .findByToken("old-token");

        verify(refreshTokenRepository)
                .delete(oldToken);

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    @Test
    void rotateRefreshToken_shouldThrowExceptionWhenOldTokenNotFound() {

        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.rotateRefreshToken("invalid-token")
        );

        assertEquals(
                "Refresh token not found",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("invalid-token");

        verify(refreshTokenRepository, never())
                .delete(any(RefreshToken.class));

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }

    @Test
    void rotateRefreshToken_shouldThrowExceptionWhenOldTokenExpired() {

        User user = new User();
        user.setUsername("ajeet");

        RefreshToken expiredToken = new RefreshToken();

        expiredToken.setToken("expired-token");

        expiredToken.setExpiryDate(
                Instant.now().minusSeconds(3600)
        );

        expiredToken.setUser(user);

        when(refreshTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(expiredToken));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.rotateRefreshToken("expired-token")
        );

        assertEquals(
                "Refresh token has expired",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("expired-token");

        verify(refreshTokenRepository)
                .delete(expiredToken);

        verify(refreshTokenRepository, never())
                .save(any(RefreshToken.class));
    }
}
