package com.ajeet.hospital.dto;

import com.ajeet.hospital.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private Role role;
    private String name;
    private String email;
    private String phone;
    private boolean enabled;
}