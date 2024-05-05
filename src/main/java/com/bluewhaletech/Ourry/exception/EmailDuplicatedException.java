package com.bluewhaletech.Ourry.exception;

public class EmailDuplicatedException extends MemberException {
    public EmailDuplicatedException(String message) {
        super(ErrorCode.EMAIL_DUPLICATED, message);
    }
}