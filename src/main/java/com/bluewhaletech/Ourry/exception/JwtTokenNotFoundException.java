package com.bluewhaletech.Ourry.exception;

public class JwtTokenNotFoundException extends MemberException {
    public JwtTokenNotFoundException(String message) {
        super(ErrorCode.JWT_TOKEN_NOT_FOUND);
    }
}
