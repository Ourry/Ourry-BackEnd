package com.bluewhaletech.Ourry.exception;

public class ReportHistoryExistException extends ReportException {
    public ReportHistoryExistException(String message) {
        super(ErrorCode.REPORT_HISTORY_EXIST, message);
    }
}