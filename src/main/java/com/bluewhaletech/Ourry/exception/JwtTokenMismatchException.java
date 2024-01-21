package com.bluewhaletech.Ourry.exception;

public class JwtTokenMismatchException extends MemberException {
    public JwtTokenMismatchException(String message) {
        super(ErrorCode.JWT_TOKEN_MISMATCH);
    }
}