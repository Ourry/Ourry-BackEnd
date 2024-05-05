package com.bluewhaletech.Ourry.exception;

public class NicknameDuplicatedException extends MemberException {
    public NicknameDuplicatedException(String message) {
        super(ErrorCode.NICKNAME_DUPLICATED, message);
    }
}