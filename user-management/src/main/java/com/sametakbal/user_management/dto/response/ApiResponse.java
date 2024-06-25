package com.sametakbal.user_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;

    public ApiResponse(String message) {
        this.message = message;
        this.success = true;
    }
}
