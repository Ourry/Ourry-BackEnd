package com.bluewhaletech.Ourry.exception;

public abstract class ArticleException extends BusinessException {
    public ArticleException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}