package com.bluewhaletech.Ourry.exception;

public class PasswordMismatchException extends MemberException {
    public PasswordMismatchException(String message) {
        super(ErrorCode.PASSWORD_MISMATCH);
    }
}