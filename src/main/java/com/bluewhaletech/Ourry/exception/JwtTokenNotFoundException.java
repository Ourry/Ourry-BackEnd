package com.bluewhaletech.Ourry.exception;

import io.jsonwebtoken.JwtException;

public class JwtTokenNotFoundException extends JwtException {
    public JwtTokenNotFoundException(String message) {
        super(message);
    }
}