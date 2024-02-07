package com.bluewhaletech.Ourry.exception;

public class MemberEmailDuplicationException extends MemberException {
    public MemberEmailDuplicationException(String message) {
        super(ErrorCode.EMAIL_DUPLICATION, message);
    }
}