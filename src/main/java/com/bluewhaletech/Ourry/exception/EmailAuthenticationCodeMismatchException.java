package com.bluewhaletech.Ourry.exception;

public class EmailAuthenticationCodeMismatchException extends MemberException {
    public EmailAuthenticationCodeMismatchException(String message) {
        super(ErrorCode.EMAIL_AUTHENTICATION_CODE_MISMATCH, message);
    }
}
