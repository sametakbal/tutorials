package com.sametakbal.user_management.dto.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String emailOrUsername;
    private String password;
}
