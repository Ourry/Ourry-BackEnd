package com.bluewhaletech.Ourry.exception;

public class ReportToOneselfException extends ReportException{
    public ReportToOneselfException(String message) {
        super(ErrorCode.REPORT_TO_ONESELF, message);
    }
}