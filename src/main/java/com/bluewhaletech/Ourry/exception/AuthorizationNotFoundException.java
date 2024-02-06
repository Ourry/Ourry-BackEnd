package com.bluewhaletech.Ourry.exception;

import io.jsonwebtoken.JwtException;

public class AuthorizationNotFoundException extends JwtException {
    public AuthorizationNotFoundException(String message) {
        super(message);
    }
}