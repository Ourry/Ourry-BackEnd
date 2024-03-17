package com.bluewhaletech.Ourry.exception;

public class EmptyJwtException extends AuthException {
    public EmptyJwtException(String message) {
        super(ErrorCode.EMPTY_JWT, message);
    }
}