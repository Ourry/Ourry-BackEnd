package com.bluewhaletech.Ourry.exception;

public class JwtTokenNotExpiredException extends MemberException{
    public JwtTokenNotExpiredException(String message) {
        super(ErrorCode.JWT_TOKEN_NOT_EXPIRED);
    }
}