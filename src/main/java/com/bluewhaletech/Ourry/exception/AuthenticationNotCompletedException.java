package com.bluewhaletech.Ourry.exception;

public class AuthenticationNotCompletedException extends MemberException {
    public AuthenticationNotCompletedException(String message) {
        super(ErrorCode.AUTHENTICATION_NOT_COMPLETED);
    }
}
