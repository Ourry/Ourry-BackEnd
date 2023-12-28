package com.bluewhaletech.Ourry.exception;

public class AuthenticationCodeExpirationException extends MemberException {
    public AuthenticationCodeExpirationException(String message) {
        super(ErrorCode.AUTHENTICATION_CODE_EXPIRATION);
    }
}
