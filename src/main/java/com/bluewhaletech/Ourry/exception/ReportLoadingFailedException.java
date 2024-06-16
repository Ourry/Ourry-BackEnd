package com.bluewhaletech.Ourry.exception;

public class ReportLoadingFailedException extends ReportException {
    public ReportLoadingFailedException(String message) {
        super(ErrorCode.REPORT_LOADING_FAILED, message);
    }
}