package com.bluewhaletech.Ourry.exception;

public abstract class CategoryException extends BusinessException {
    public CategoryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
