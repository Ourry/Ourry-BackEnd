package com.bluewhaletech.Ourry.exception;

public class AuthenticationCodeMismatchException extends MemberException {
    public AuthenticationCodeMismatchException(String message) {
        super(ErrorCode.AUTHENTICATION_CODE_MISMATCH);
    }
}
