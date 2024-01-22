package com.bluewhaletech.Ourry.exception;

public class JwtTokenExpiredException extends MemberException {
    public JwtTokenExpiredException(String message) {
        super(ErrorCode.JWT_TOKEN_EXPIRED);
    }
}
