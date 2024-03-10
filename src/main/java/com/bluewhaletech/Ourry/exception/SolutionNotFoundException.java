package com.bluewhaletech.Ourry.exception;

public class SolutionNotFoundException extends ArticleException {
    public SolutionNotFoundException(String message) {
        super(ErrorCode.SOLUTION_NOT_FOUND, message);
    }
}