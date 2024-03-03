package com.bluewhaletech.Ourry.exception;

public class QuestionLoadingFailedException extends ArticleException {
    public QuestionLoadingFailedException(String message) {
        super(ErrorCode.QUESTION_LOADING_FAILED, message);
    }
}