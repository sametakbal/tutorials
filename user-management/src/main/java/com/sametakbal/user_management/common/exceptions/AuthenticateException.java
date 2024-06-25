package com.sametakbal.user_management.common.exceptions;

public class AuthenticateException extends RuntimeException{
    public AuthenticateException(String message) {
        super(message);
    }
}
