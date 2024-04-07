package com.bluewhaletech.Ourry.exception;

public class FcmTokenNotFoundException extends MemberException {
    public FcmTokenNotFoundException(String message) {
        super(ErrorCode.FCM_TOKEN_NOT_FOUND, message);
    }
}