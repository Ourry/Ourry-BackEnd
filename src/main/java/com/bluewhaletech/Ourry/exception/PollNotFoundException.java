package com.bluewhaletech.Ourry.exception;

public class PollNotFoundException extends ArticleException {
    public PollNotFoundException(String message) {
        super(ErrorCode.POLL_NOT_FOUND, message);
    }
}
