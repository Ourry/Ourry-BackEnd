package com.bluewhaletech.Ourry.exception;

public class EmailAuthenticationNotCompletedException extends MemberException {
    public EmailAuthenticationNotCompletedException(String message) {
        super(ErrorCode.EMAIL_AUTHENTICATION_NOT_COMPLETED, message);
    }
}
