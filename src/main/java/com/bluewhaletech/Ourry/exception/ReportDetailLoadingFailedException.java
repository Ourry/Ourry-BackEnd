package com.bluewhaletech.Ourry.exception;

public class ReportDetailLoadingFailedException extends ReportException {
    public ReportDetailLoadingFailedException(String message) {
        super(ErrorCode.REPORT_DETAIL_LOADING_FAILED, message);
    }
}