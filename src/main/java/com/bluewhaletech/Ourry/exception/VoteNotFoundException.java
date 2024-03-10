package com.bluewhaletech.Ourry.exception;

public class VoteNotFoundException extends ArticleException {
    public VoteNotFoundException(String message) {
        super(ErrorCode.SOLUTION_NOT_FOUND, message);
    }
}
