package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.RefreshTokenRequest;
import com.ajeet.hospital.service.AuthService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;

import com.ajeet.hospital.dto.LoginRequest;
import com.ajeet.hospital.dto.LoginResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
@ImportAutoConfiguration(exclude = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class
})
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_shouldReturn201Created() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "username": "ajeet",
                                "password": "123456"
                            }
                        """)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void register_shouldReturn400WhenUsernameIsBlank() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "username": "",
                            "password": "123456"
                        }
                    """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn200AndLoginResponse() throws Exception {

        LoginResponse loginResponse = new LoginResponse(
                "access-token",
                "refresh-token",
                "ajeet",
                "PATIENT"
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "username": "ajeet",
                            "password": "123456"
                        }
                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("refresh-token"))
                .andExpect(jsonPath("$.username")
                        .value("ajeet"))
                .andExpect(jsonPath("$.role")
                        .value("PATIENT"));
    }

    @Test
    void login_shouldReturn400WhenUsernameIsBlank() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "username": "",
                            "password": "123456"
                        }
                    """)
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .login(any(LoginRequest.class));
    }

    @Test
    void refreshToken_shouldReturn200AndNewTokens() throws Exception {

        LoginResponse loginResponse = new LoginResponse(
                "new-access-token",
                "new-refresh-token",
                "ajeet",
                "PATIENT"
        );

        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "refreshToken": "old-refresh-token"
                        }
                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("new-refresh-token"))
                .andExpect(jsonPath("$.username")
                        .value("ajeet"))
                .andExpect(jsonPath("$.role")
                        .value("PATIENT"));
    }

    @Test
    void logout_shouldReturn200AndSuccessMessage() throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "refreshToken": "refresh-token"
                        }
                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));

        verify(authService)
                .logout("refresh-token");
    }

    @Test
    void oauth2Success_shouldRedirectToFrontend() {

        OAuth2User oauth2User = mock(OAuth2User.class);

        LoginResponse loginResponse =
                new LoginResponse(
                        "google-access-token",
                        "google-refresh-token",
                        "ajeet@gmail.com",
                        "PATIENT"
                );

        when(authService.googleLogin(oauth2User))
                .thenReturn(loginResponse);

        AuthController controller =
                new AuthController(authService);

        ReflectionTestUtils.setField(
                controller,
                "frontendUrl",
                "http://localhost:5173"
        );

        RedirectView result =
                controller.oauth2Success(oauth2User);

        assertNotNull(result);

        assertEquals(
                "http://localhost:5173/oauth2/callback"
                        + "#accessToken=google-access-token"
                        + "&refreshToken=google-refresh-token"
                        + "&username=ajeet@gmail.com"
                        + "&role=PATIENT",
                result.getUrl()
        );
    }
    @Test
    void oauth2Failure_shouldReturn401() throws Exception {

        mockMvc.perform(
                        get("/api/auth/oauth2/failure")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Google login failed"));
    }
}
