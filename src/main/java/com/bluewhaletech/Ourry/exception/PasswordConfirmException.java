package com.bluewhaletech.Ourry.exception;

public class PasswordConfirmException extends MemberException {
    public PasswordConfirmException(String message) {
        super(ErrorCode.PASSWORD_MISMATCH, message);
    }
}