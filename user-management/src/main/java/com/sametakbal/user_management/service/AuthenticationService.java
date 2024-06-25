package com.sametakbal.user_management.service;

import com.sametakbal.user_management.dto.request.AuthenticationRequest;
import com.sametakbal.user_management.dto.request.RegistrationRequest;
import com.sametakbal.user_management.dto.response.DataResponse;

public interface AuthenticationService {
    void register(RegistrationRequest registrationRequest);

    DataResponse<String> authenticate(AuthenticationRequest request);

    void confirmToken(String token);

}
