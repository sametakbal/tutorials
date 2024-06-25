package com.sametakbal.user_management.controller;

import com.sametakbal.user_management.common.exceptions.AuthenticateException;
import com.sametakbal.user_management.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(AuthenticateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse> handleUserAccessDenied(AuthenticateException ex) {
        return ResponseEntity.ok(ApiResponse.builder().message(ex.getMessage()).success(false).build());
    }
}
