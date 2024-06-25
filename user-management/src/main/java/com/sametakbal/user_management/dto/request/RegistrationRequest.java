package com.sametakbal.user_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {

    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
}
