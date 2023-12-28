package com.bluewhaletech.Ourry.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
}