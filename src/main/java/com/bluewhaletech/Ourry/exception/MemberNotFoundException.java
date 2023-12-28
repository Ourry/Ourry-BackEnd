package com.bluewhaletech.Ourry.exception;

public class MemberNotFoundException extends MemberException {
    public MemberNotFoundException(String message) {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}