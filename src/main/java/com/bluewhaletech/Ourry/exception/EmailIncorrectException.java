package com.bluewhaletech.Ourry.exception;

public class EmailIncorrectException extends MemberException {
    public EmailIncorrectException(String message) {
        super(ErrorCode.EMAIL_INCORRECT);
    }
}
