package com.bluewhaletech.Ourry.exception;

public class AlreadyLoggedOutException extends AuthException {
    public AlreadyLoggedOutException(String message) {
        super(ErrorCode.ALREADY_LOGGED_OUT, message);
    }
}