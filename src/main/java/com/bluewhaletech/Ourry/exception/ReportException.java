package com.bluewhaletech.Ourry.exception;

public abstract class ReportException extends BusinessException {
    public ReportException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}