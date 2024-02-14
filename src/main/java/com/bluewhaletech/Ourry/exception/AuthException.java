package com.bluewhaletech.Ourry.exception;

public abstract class AuthException extends BusinessException {
    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
