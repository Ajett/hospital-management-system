package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.LoginRequest;
import com.ajeet.hospital.dto.LoginResponse;
import com.ajeet.hospital.dto.RefreshTokenRequest;
import com.ajeet.hospital.dto.RegisterRequest;
import com.ajeet.hospital.entity.Patient;
import com.ajeet.hospital.entity.RefreshToken;
import com.ajeet.hospital.entity.Role;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.PatientRepository;
import com.ajeet.hospital.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldRegisterUserSuccessfully() {

        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ajeet");
        request.setPassword("123456");

        when(userRepository.findByUsername("ajeet"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        authService.register(request);

        // Then
        verify(userRepository)
                .save(any(User.class));

        verify(patientRepository)
                .save(any(Patient.class));
    }

    @Test
    void register_shouldThrowExceptionWhenUsernameAlreadyExists() {

        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ajeet");
        request.setPassword("123456");

        User existingUser = new User();
        existingUser.setUsername("ajeet");

        when(userRepository.findByUsername("ajeet"))
                .thenReturn(Optional.of(existingUser));

        // When + Then
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Username already exists",
                exception.getMessage()
        );

        // Verify user was NOT saved
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void login_shouldReturnLoginResponseForValidCredentials() {

        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("ajeet");
        request.setPassword("123456");

        User user = new User();
        user.setUsername("ajeet");
        user.setPassword("encoded-password");
        user.setRole(Role.PATIENT);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        // Authentication succeeds
        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        "ajeet",
                        null
                )
        );

        // User exists
        when(userRepository.findByUsername("ajeet"))
                .thenReturn(Optional.of(user));

        // JWT generation
        when(jwtService.generateToken(
                "ajeet",
                "PATIENT"
        )).thenReturn("access-token");

        // Refresh token generation
        when(refreshTokenService.createRefreshToken("ajeet"))
                .thenReturn(refreshToken);


        // When
        LoginResponse response =
                authService.login(request);


        // Then
        assertEquals(
                "access-token",
                response.getAccessToken()
        );

        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "ajeet",
                response.getUsername()
        );

        assertEquals(
                "PATIENT",
                response.getRole()
        );
    }

    @Test
    void login_shouldThrowExceptionForInvalidCredentials() {

        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("ajeet");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenThrow(
                new BadCredentialsException("Invalid username or password")
        );

        // When + Then
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        // JWT should NOT be generated
        verify(jwtService, never())
                .generateToken(any(), any());

        // User should NOT be searched
        verify(userRepository, never())
                .findByUsername(anyString());

        // Refresh token should NOT be created
        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }

    @Test
    void login_shouldThrowExceptionWhenUserNotFound() {

        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("ajeet");
        request.setPassword("123456");

        // Authentication succeeds
        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        "ajeet",
                        null
                )
        );

        // User does NOT exist
        when(userRepository.findByUsername("ajeet"))
                .thenReturn(Optional.empty());

        // When + Then
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(request)
                );

        // Check exception message
        assertEquals(
                "User not found",
                exception.getMessage()
        );

        // JWT should NOT be generated
        verify(jwtService, never())
                .generateToken(any(), any());

        // Refresh token should NOT be created
        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }

    @Test
    void refreshToken_shouldReturnNewLoginResponse() {

        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        User user = new User();
        user.setUsername("ajeet");
        user.setRole(Role.PATIENT);
        user.setPassword("encoded-password");

        RefreshToken rotatedRefreshToken = new RefreshToken();
        rotatedRefreshToken.setToken("rotated-refresh-token");
        rotatedRefreshToken.setUser(user);

        // Refresh token is valid and rotated
        when(refreshTokenService.rotateRefreshToken(
                "old-refresh-token"
        )).thenReturn(rotatedRefreshToken);

        // New access token
        when(jwtService.generateToken(
                "ajeet",
                "PATIENT"
        )).thenReturn("new-access-token");

        // New refresh token created by generateLoginResponse()
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("new-refresh-token");
        newRefreshToken.setUser(user);

        when(refreshTokenService.createRefreshToken("ajeet"))
                .thenReturn(newRefreshToken);


        // When
        LoginResponse response =
                authService.refreshToken(request);


        // Then
        assertEquals(
                "new-access-token",
                response.getAccessToken()
        );

        assertEquals(
                "new-refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "ajeet",
                response.getUsername()
        );

        assertEquals(
                "PATIENT",
                response.getRole()
        );
    }

    @Test
    void refreshToken_shouldThrowExceptionForInvalidRefreshToken() {

        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-refresh-token");

        when(refreshTokenService.rotateRefreshToken(
                "invalid-refresh-token"
        )).thenThrow(
                new RuntimeException("Invalid or expired refresh token")
        );

        // When + Then
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.refreshToken(request)
                );

        // Check exception message
        assertEquals(
                "Invalid or expired refresh token",
                exception.getMessage()
        );

        // JWT should NOT be generated
        verify(jwtService, never())
                .generateToken(any(), any());

        // New refresh token should NOT be created
        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }

    @Test
    void logout_shouldDeleteRefreshToken() {

        // Given
        String refreshToken = "refresh-token";

        // When
        authService.logout(refreshToken);

        // Then
        verify(refreshTokenService)
                .deleteByToken(refreshToken);
    }

    @Test
    void googleLogin_shouldCreateNewPatientAndReturnLoginResponse() {

        // Given
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email"))
                .thenReturn("ajeet@gmail.com");

        // User does not already exist
        when(userRepository.findByUsername("ajeet@gmail.com"))
                .thenReturn(Optional.empty());

        // Password encoding
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        // Save the newly created user
        User savedUser = new User();
        savedUser.setUsername("ajeet@gmail.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.PATIENT);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // JWT
        when(jwtService.generateToken(
                "ajeet@gmail.com",
                "PATIENT"
        )).thenReturn("access-token");

        // Refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(refreshTokenService.createRefreshToken(
                "ajeet@gmail.com"
        )).thenReturn(refreshToken);


        // When
        LoginResponse response =
                authService.googleLogin(oauth2User);


        // Then
        assertEquals(
                "access-token",
                response.getAccessToken()
        );

        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "ajeet@gmail.com",
                response.getUsername()
        );

        assertEquals(
                "PATIENT",
                response.getRole()
        );

        verify(userRepository)
                .save(any(User.class));

        verify(patientRepository)
                .save(any(Patient.class));
    }

    @Test
    void googleLogin_shouldLoginExistingUserWithoutCreatingNewUser() {

        // Given
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email"))
                .thenReturn("ajeet@gmail.com");


        // User already exists
        User existingUser = new User();

        existingUser.setUsername("ajeet@gmail.com");
        existingUser.setPassword("encoded-password");
        existingUser.setRole(Role.PATIENT);

        when(userRepository.findByUsername("ajeet@gmail.com"))
                .thenReturn(Optional.of(existingUser));


        // JWT
        when(jwtService.generateToken(
                "ajeet@gmail.com",
                "PATIENT"
        )).thenReturn("access-token");


        // Refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(refreshTokenService.createRefreshToken(
                "ajeet@gmail.com"
        )).thenReturn(refreshToken);


        // When
        LoginResponse response =
                authService.googleLogin(oauth2User);


        // Then
        assertEquals(
                "access-token",
                response.getAccessToken()
        );

        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "ajeet@gmail.com",
                response.getUsername()
        );

        assertEquals(
                "PATIENT",
                response.getRole()
        );


        // IMPORTANT:
        // Existing user should NOT be saved again
        verify(userRepository, never())
                .save(any(User.class));

        // Existing user should not get a new password
        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void googleLogin_shouldThrowExceptionWhenEmailIsNotProvided() {

        // Given
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email"))
                .thenReturn(null);

        // When + Then
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.googleLogin(oauth2User)
                );

        // Check exception message
        assertEquals(
                "Email not provided by Google",
                exception.getMessage()
        );

        // No database lookup should happen
        verify(userRepository, never())
                .findByUsername(anyString());

        // No password should be generated/encoded
        verify(passwordEncoder, never())
                .encode(anyString());

        // No JWT should be generated
        verify(jwtService, never())
                .generateToken(any(), any());

        // No refresh token should be created
        verify(refreshTokenService, never())
                .createRefreshToken(anyString());
    }
}
