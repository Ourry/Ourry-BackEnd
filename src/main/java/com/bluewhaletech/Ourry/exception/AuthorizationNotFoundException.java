package com.bluewhaletech.Ourry.exception;

public class AuthorizationNotFoundException extends MemberException {
    public AuthorizationNotFoundException(String message) {
        super(ErrorCode.AUTHORIZATION_NOT_FOUND);
    }
}