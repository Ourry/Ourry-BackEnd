package com.bluewhaletech.Ourry.exception;

public class JwtMismatchException extends AuthException {
    public JwtMismatchException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}