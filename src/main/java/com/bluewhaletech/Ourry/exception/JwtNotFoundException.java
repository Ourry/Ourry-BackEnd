package com.bluewhaletech.Ourry.exception;

public class JwtNotFoundException extends AuthException{
    public JwtNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
