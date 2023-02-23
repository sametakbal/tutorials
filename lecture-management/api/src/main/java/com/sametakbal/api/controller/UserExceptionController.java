package com.sametakbal.api.controller;

import com.sametakbal.api.common.GeneralException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class UserExceptionController {
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> exception(GeneralException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
