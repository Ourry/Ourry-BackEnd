package com.bluewhaletech.Ourry.exception;

import io.jsonwebtoken.JwtException;

public class JwtTokenMismatchException extends JwtException {
    public JwtTokenMismatchException(String message) {
        super(message);
    }
}