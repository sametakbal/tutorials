package com.sametakbal.user_management.controller;

import com.sametakbal.user_management.dto.request.AuthenticationRequest;
import com.sametakbal.user_management.dto.request.RegistrationRequest;
import com.sametakbal.user_management.dto.response.ApiResponse;
import com.sametakbal.user_management.dto.response.DataResponse;
import com.sametakbal.user_management.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        service.register(registrationRequest);
        return ResponseEntity.ok(new ApiResponse("User registered successfully"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<DataResponse<String>> login(@RequestBody @Valid AuthenticationRequest request) {
        var response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
