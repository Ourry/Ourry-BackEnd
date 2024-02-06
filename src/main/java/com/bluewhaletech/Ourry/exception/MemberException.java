package com.bluewhaletech.Ourry.exception;

public abstract class MemberException extends BusinessException {
    public MemberException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}