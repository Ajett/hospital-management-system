package com.ajeet.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;

    public LoginResponse(
            String accessToken,
            String refreshToken,
            String username,
            String role) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }
}
